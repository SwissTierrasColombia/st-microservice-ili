package com.ai.st.microservice.ili.rabbitmq.listerners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.business.ConceptBusiness;
import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.Ili2pgExportReferenceDto;
import com.ai.st.microservice.ili.dto.Ili2pgImportReferenceDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IliExportResultDto;
import com.ai.st.microservice.ili.dto.IliProcessQueueDto;
import com.ai.st.microservice.ili.dto.IlivalidatorBackgroundDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
import com.ai.st.microservice.ili.dto.ResultExportDto;
import com.ai.st.microservice.ili.dto.ResultImportDto;
import com.ai.st.microservice.ili.dto.ValidationDto;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.services.Ili2pgService;
import com.ai.st.microservice.ili.services.IlivalidatorService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;
import com.ai.st.microservice.ili.services.ZipService;

@Component
public class RabbitMQIliListerner {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Ili2pgService ili2pgService;

    @Autowired
    private ZipService zipService;

    @Autowired
    private IlivalidatorService ilivalidatorService;

    @Autowired
    private RabbitMQSenderService rabbitService;

    @Autowired
    private VersionBusiness versionBusiness;

    @Value("${st.filesDirectory}")
    private String stFilesDirectory;

    @Value("${st.temporalDirectory}")
    private String stTemporalDirectory;

    @Value("${iliProcesses.srs}")
    private String srsDefault;

    @RabbitListener(queues = "${st.rabbitmq.queueIli.queue}", concurrency = "${st.rabbitmq.queueIli.concurrency}")
    public void iliProcess(IliProcessQueueDto data) {

        log.info("ili process started");

        if (data.getType().equals(IliProcessQueueDto.VALIDATOR)) {
            this.ilivalidator(data.getIlivalidatorData());
        }

        if (data.getType().equals(IliProcessQueueDto.INTEGRATOR)) {
            this.integration(data.getIntegrationData());
        }

        if (data.getType().equals(IliProcessQueueDto.EXPORT)) {
            this.export(data.getExportData());
        }

        if (data.getType().equals(IliProcessQueueDto.IMPORT_REFERENCE)) {
            this.importReference(data.getImportReferenceData());
        }

        if (data.getType().equals(IliProcessQueueDto.EXPORT_REFERENCE)) {
            this.exportReference(data.getExportReferenceData());
        }

    }

    public void ilivalidator(IlivalidatorBackgroundDto data) {

        log.info("validation started #" + data.getRequestId());

        ValidationDto validationDto = new ValidationDto();
        Boolean validation = false;

        try {

            VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(),
                    ConceptBusiness.CONCEPT_OPERATION);
            if (versionData != null) {

                Path path = Paths.get(data.getPathFile());
                String fileName = path.getFileName().toString();
                String fileExtension = FilenameUtils.getExtension(fileName);

                String pathFileXTF = "";

                String nameDirectory = "ili_process_validation_" + RandomStringUtils.random(7, false, true);
                Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);
                Path tmpDirectoryLog = Files.createTempDirectory(Paths.get(stTemporalDirectory), RandomStringUtils.random(7, false, true));

                if (fileExtension.equalsIgnoreCase("zip")) {

                    List<String> paths = zipService.unzip(data.getPathFile(), new File(tmpDirectory.toString()));
                    pathFileXTF = tmpDirectory.toString() + File.separator + paths.get(0);

                } else if (fileExtension.equalsIgnoreCase("xtf")) {
                    pathFileXTF = data.getPathFile();
                }

                if (pathFileXTF.isEmpty()) {
                    log.error("there is not file xtf.");
                } else {

                    String logFileValidation = Paths.get(tmpDirectoryLog.toString(), "ilivalidator.log").toString();
                    String logFileValidationXTF = Paths.get(tmpDirectory.toString(), "ilivalidator.xtf").toString();

                    String pathTomlFile = null;
                    if (data.getSkipGeometryValidation()) {

                        try {
                            final Path pathToml = Files.createTempFile("myTomlFile", ".toml");

                            String dataFile = "[\"PARAMETER\"]\n" +
                                    "defaultGeometryTypeValidation=\"off\"";

                            // Writing data here
                            byte[] buf = dataFile.getBytes();
                            Files.write(pathToml, buf);

                            // Delete file on exit
                            pathToml.toFile().deleteOnExit();

                            pathTomlFile = pathToml.toFile().getAbsolutePath();

                        } catch (IOException e) {
                            log.error("Error creating toml file: " + e.getMessage());
                        }

                    }

                    validation = ilivalidatorService.validate(pathFileXTF, versionData.getUrl(),
                            versionData.getModels(), null, logFileValidation, logFileValidationXTF, pathTomlFile);

                    log.info("validation successful with result: " + validation);

                    if (!validation) {
                        validationDto.setErrors(searchErrors(logFileValidation));
                        validationDto.setLog(logFileValidation);
                    } else {
                        try {
                            FileUtils.deleteDirectory(tmpDirectoryLog.toFile());
                        } catch (Exception e) {
                            log.error("It has not been possible delete the directory (log): " + e.getMessage());
                        }
                    }

                    try {
                        FileUtils.deleteDirectory(tmpDirectory.toFile());
                    } catch (Exception e) {
                        log.error("It has not been possible delete the directory: " + e.getMessage());
                    }

                }

            }

        } catch (Exception e) {
            log.error("validation failed # " + data.getRequestId() + " : " + e.getMessage());
            validationDto.setErrors(new ArrayList<>(Collections.singletonList(e.getMessage())));
        }


        validationDto.setIsValid(validation);
        validationDto.setRequestId(data.getRequestId());
        validationDto.setSupplyRequestedId(data.getSupplyRequestedId());
        validationDto.setFilenameTemporal(data.getFilenameTemporal());
        validationDto.setUserCode(data.getUserCode());
        validationDto.setObservations(data.getObservations());
        validationDto.setGeometryValidated(!data.getSkipGeometryValidation());
        validationDto.setSkipErrors(data.getSkipErrors());

        rabbitService.sendStatsValidation(validationDto);
    }

    public void integration(Ili2pgIntegrationCadastreRegistrationWithoutFilesDto data) {

        log.info("integration started #" + data.getIntegrationId());

        IntegrationStatDto integrationStatDto = null;

        try {

            VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(),
                    ConceptBusiness.CONCEPT_INTEGRATION);
            if (versionData != null) {

                String nameDirectory = "ili_process_import_" + RandomStringUtils.random(7, false, true);
                Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

                List<String> pathsCadastre = zipService.unzip(data.getCadastrePathXTF(),
                        new File(tmpDirectory.toString()));
                String pathFileCadastre = tmpDirectory.toString() + File.separator + pathsCadastre.get(0);

                List<String> pathsRegistration = zipService.unzip(data.getRegistrationPathXTF(),
                        new File(tmpDirectory.toString()));
                String pathFileRegistration = tmpDirectory.toString() + File.separator + pathsRegistration.get(0);

                String cadastreLogFileSchemaImport = tmpDirectory.toString() + File.separator
                        + "cadastre_schema_import.log";
                String cadastreLogFileImport = tmpDirectory.toString() + File.separator + "cadastre_import.log";

                String registrationLogFileSchemaImport = tmpDirectory.toString() + File.separator
                        + "registration_schema_import.log";
                String registrationLogFileImport = tmpDirectory.toString() + File.separator + "registration_import.log";

                integrationStatDto = ili2pgService.integration(pathFileCadastre, cadastreLogFileSchemaImport,
                        cadastreLogFileImport, pathFileRegistration, registrationLogFileSchemaImport,
                        registrationLogFileImport, versionData.getUrl(), srsDefault, versionData.getModels(),
                        data.getDatabaseHost(), data.getDatabasePort(), data.getDatabaseName(),
                        data.getDatabaseSchema(), data.getDatabaseUsername(), data.getDatabasePassword(),
                        data.getVersionModel());

                if (!integrationStatDto.isStatus()) {
                    List<String> errorsList = new ArrayList<>(searchErrors(cadastreLogFileImport));
                    errorsList.addAll(searchErrors(registrationLogFileImport));
                    integrationStatDto.setErrors(errorsList);
                }

                try {
                    FileUtils.deleteDirectory(tmpDirectory.toFile());
                } catch (Exception e) {
                    log.error("It has not been possible delete the directory: " + e.getMessage());
                }

                log.info("Integration finished with result: " + integrationStatDto.isStatus());
            }

        } catch (Exception e) {
            log.error("Integration failed # " + data.getIntegrationId());
            log.error("Integration error  " + e.getMessage());
            integrationStatDto.setErrors(new ArrayList<>(Collections.singletonList(e.getMessage())));

            integrationStatDto = new IntegrationStatDto();
            integrationStatDto.setStatus(false);
        }

        integrationStatDto.setIntegrationId(data.getIntegrationId());
        rabbitService.sendStats(integrationStatDto);
    }

    public void export(Ili2pgExportDto data) {

        log.info("export started #" + data.getIntegrationId());

        IliExportResultDto resultDto = new IliExportResultDto();

        try {

            VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(),
                    ConceptBusiness.CONCEPT_INTEGRATION);
            if (versionData != null) {

                IntegrationStatDto stats = null;
                if (data.getWithStats()) {
                    stats = ili2pgService.getIntegrationStats(data.getDatabaseHost(), data.getDatabasePort(),
                            data.getDatabaseName(), data.getDatabaseUsername(), data.getDatabasePassword(),
                            data.getDatabaseSchema(), data.getVersionModel());
                }

                String nameDirectory = "ili_process_export_" + RandomStringUtils.random(7, false, true);
                Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

                String logExport = Paths.get(tmpDirectory.toString(), "export.log").toString();

                Boolean result = ili2pgService.exportToXtf(data.getPathFileXTF(), logExport, versionData.getUrl(),
                        srsDefault, versionData.getModels(), data.getDatabaseHost(), data.getDatabasePort(),
                        data.getDatabaseName(), data.getDatabaseSchema(), data.getDatabaseUsername(),
                        data.getDatabasePassword());

                if (!result) {
                    resultDto.setErrors(searchErrors(logExport));
                }

                if (result) {
                    log.info("zipping export file");
                    String zipName = RandomStringUtils.random(20, true, false);
                    Path path = Paths.get(data.getPathFileXTF());
                    String originalFilename = path.getFileName().toString();
                    String fileExtension = FilenameUtils.getExtension(originalFilename);
                    String fileName = RandomStringUtils.random(20, true, false) + "." + fileExtension;
                    String urlBase = path.getParent().toString();
                    urlBase = ZipService.removeAccents(urlBase);
                    String urlZipFile = ZipService.zipping(new File(data.getPathFileXTF()), zipName, fileName, urlBase);
                    resultDto.setPathFile(urlZipFile);
                    log.info("export file zipped");

                    try {
                        FileUtils.deleteQuietly(new File(data.getPathFileXTF()));
                    } catch (Exception e) {
                        log.error("It has not been possible delete the file exported: " + e.getMessage());
                    }

                } else {
                    resultDto.setPathFile(null);
                }

                resultDto.setStatus(result);
                resultDto.setStats(stats);
                resultDto.setModelVersion(data.getVersionModel());

                log.info("Export finished with result: " + resultDto.isStatus());

                try {
                    FileUtils.deleteDirectory(tmpDirectory.toFile());
                } catch (Exception e) {
                    log.error("It has not been possible delete the directory: " + e.getMessage());
                }

            }

        } catch (Exception e) {
            log.error("Export failed # " + data.getIntegrationId());
            log.error("Export error  " + e.getMessage());

            resultDto.setStatus(false);
        }

        resultDto.setIntegrationId(data.getIntegrationId());
        rabbitService.sendResultExport(resultDto);
    }

    public void importReference(Ili2pgImportReferenceDto data) {

        log.info("import reference started # " + data.getReference());

        ResultImportDto resultImportDto = new ResultImportDto();
        resultImportDto.setResult(false);
        resultImportDto.setPathFile(data.getPathXTF());
        resultImportDto.setReference(data.getReference());

        try {

            VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(), data.getConceptId());
            if (versionData != null) {
                Path path = Paths.get(data.getPathXTF());
                String fileName = path.getFileName().toString();
                String fileExtension = FilenameUtils.getExtension(fileName);

                String pathFileXTF = "";

                String nameDirectory = "ili_process_import_" + RandomStringUtils.random(7, false, true);
                Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

                if (fileExtension.equalsIgnoreCase("zip")) {

                    List<String> paths = zipService.unzip(data.getPathXTF(), new File(tmpDirectory.toString()));
                    pathFileXTF = tmpDirectory.toString() + File.separator + paths.get(0);

                } else if (fileExtension.equalsIgnoreCase("xtf")) {
                    pathFileXTF = data.getPathXTF();
                }

                if (pathFileXTF.isEmpty()) {
                    log.error("No existe archivo xtf para realizar el proceso.");
                } else {

                    String logFileSchemaImport = tmpDirectory.toString() + File.separator + "schema_import.log";
                    String logFileImport = tmpDirectory.toString() + File.separator + "import.log";

                    Boolean importValid = ili2pgService.import2pg(pathFileXTF, logFileSchemaImport, logFileImport,
                            versionData.getUrl(), srsDefault, versionData.getModels(), data.getDatabaseHost(),
                            data.getDatabasePort(), data.getDatabaseName(), data.getDatabaseSchema(),
                            data.getDatabaseUsername(), data.getDatabasePassword());

                    try {
                        FileUtils.deleteDirectory(tmpDirectory.toFile());
                    } catch (Exception e) {
                        log.error("It has not been possible delete the directory: " + e.getMessage());
                    }

                    resultImportDto.setResult(importValid);
                    log.info("Import reference finished with result: " + importValid);

                }
            }

        } catch (Exception e) {
            resultImportDto.setResult(false);
            log.error("Import failed with error: " + e.getMessage());
        }

        try {
            rabbitService.sendResultToImport(resultImportDto);
        } catch (Exception e) {
            log.error("Error sending result from import: " + e.getMessage());
        }

    }

    public void exportReference(Ili2pgExportReferenceDto data) {

        log.info("export reference started #" + data.getReference());

        ResultExportDto resultExportDto = new ResultExportDto();
        resultExportDto.setResult(false);
        resultExportDto.setReference(data.getReference());

        try {

            VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(), data.getConceptId());
            if (versionData instanceof VersionDataDto) {

                String nameDirectory = "ili_process_export_" + RandomStringUtils.random(7, false, true);
                Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);
                String logExport = Paths.get(tmpDirectory.toString(), "export.log").toString();

                Boolean result = ili2pgService.exportToXtf(data.getPathFileXTF(), logExport, versionData.getUrl(),
                        srsDefault, versionData.getModels(), data.getDatabaseHost(), data.getDatabasePort(),
                        data.getDatabaseName(), data.getDatabaseSchema(), data.getDatabaseUsername(),
                        data.getDatabasePassword());

                if (result) {
                    log.info("zipping export file");
                    String zipName = RandomStringUtils.random(20, true, false);
                    Path path = Paths.get(data.getPathFileXTF());
                    String originalFilename = path.getFileName().toString();
                    String fileExtension = FilenameUtils.getExtension(originalFilename);
                    String fileName = RandomStringUtils.random(20, true, false) + "." + fileExtension;
                    String urlBase = path.getParent().toString();
                    urlBase = ZipService.removeAccents(urlBase);
                    String urlZipFile = ZipService.zipping(new File(data.getPathFileXTF()), zipName, fileName, urlBase);
                    resultExportDto.setPathFile(urlZipFile);
                    log.info("export file zipped");

                    try {
                        FileUtils.deleteQuietly(new File(data.getPathFileXTF()));
                    } catch (Exception e) {
                        log.error("It has not been possible delete the file exported: " + e.getMessage());
                    }

                } else {
                    resultExportDto.setPathFile(null);
                }

                resultExportDto.setResult(result);

                log.info("Export reference finished with result: " + result);

                try {
                    FileUtils.deleteQuietly(new File(data.getPathFileXTF()));
                } catch (Exception e) {
                    log.error("It has not been possible delete the directory: " + e.getMessage());
                }

            }

        } catch (Exception e) {
            log.error("Export reference failed # " + data.getReference());
            log.error("Export reference error  " + e.getMessage());
        }

        try {
            rabbitService.sendResultToExport(resultExportDto);
        } catch (Exception e) {
            log.error("Error sending result from export: " + e.getMessage());
        }

    }

    public List<String> searchErrors(String pathFile) {
        List<String> errors = new ArrayList<>();
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(new File(pathFile), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                boolean errorFound = line.contains("Error:");
                if (errorFound) {
                    errors.add(line);
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        return errors;
    }

}
