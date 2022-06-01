package com.ai.st.microservice.ili.services.rabbitmq.listerners;

import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.Ili2pgImportSinicDto;
import com.ai.st.microservice.ili.dto.ResultSinicImportFile;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.services.Ili2pgService;
import com.ai.st.microservice.ili.services.rabbitmq.RabbitMQSenderService;
import com.ai.st.microservice.ili.services.ZipService;
import com.ai.st.microservice.ili.services.tracing.SCMTracing;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class SinicImport {

    private final Logger log = LoggerFactory.getLogger(SinicImport.class);

    private final Ili2pgService ili2pgService;
    private final ZipService zipService;
    private final RabbitMQSenderService rabbitService;
    private final VersionBusiness versionBusiness;
    private final String stTemporalDirectory;
    private final String srsDefault;

    public SinicImport(Ili2pgService ili2pgService, ZipService zipService, RabbitMQSenderService rabbitService,
            VersionBusiness versionBusiness, String stTemporalDirectory, String srsDefault) {
        this.ili2pgService = ili2pgService;
        this.zipService = zipService;
        this.rabbitService = rabbitService;
        this.versionBusiness = versionBusiness;
        this.stTemporalDirectory = stTemporalDirectory;
        this.srsDefault = srsDefault;
    }

    public void execute(Ili2pgImportSinicDto data) {

        log.info("SINIC IMPORT STARTED ");

        updateStatus(data, ResultSinicImportFile.Status.IMPORTING);

        try {

            VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(), data.getConceptId());
            if (versionData != null) {

                String nameDirectory = "ili_sinic_process_import_" + RandomStringUtils.random(8, false, true);
                Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

                List<String> paths = zipService.unzip(data.getPathXTF(), new File(tmpDirectory.toString()));
                String pathFileXTF = tmpDirectory + File.separator + paths.get(0);

                String logFileSchemaImport = tmpDirectory + File.separator + "schema_sinic_import.log";
                String logFileImport = tmpDirectory + File.separator + "sinic_import.log";

                String models = versionData.getModels();

                boolean importValid = ili2pgService.import2pg(pathFileXTF, logFileSchemaImport, logFileImport,
                        versionData.getUrl(), srsDefault, models, data.getDatabaseHost(), data.getDatabasePort(),
                        data.getDatabaseName(), data.getDatabaseSchema(), data.getDatabaseUsername(),
                        data.getDatabasePassword());

                try {
                    FileUtils.deleteDirectory(tmpDirectory.toFile());
                } catch (Exception e) {
                    String messageError = String.format(
                            "Error eliminando el directorio de la importación del archivo SINIC : %s", e.getMessage());
                    SCMTracing.sendError(messageError);
                    log.error(messageError);
                }

                log.info("SINIC IMPORT FINISHED WITH RESULT: " + importValid);
                ResultSinicImportFile.Status status = (importValid) ? ResultSinicImportFile.Status.SUCCESS_IMPORT
                        : ResultSinicImportFile.Status.FAILED_IMPORT;
                updateStatus(data, status);

            } else {
                log.error("CONCEPT VERSION NOT FOUND: ");
                updateStatus(data, ResultSinicImportFile.Status.FAILED_IMPORT);
            }

        } catch (Exception e) {
            String messageError = String.format("Error realizando la importación del archivo SINIC %s : %s",
                    data.getReference(), e.getMessage());
            SCMTracing.sendError(messageError);
            log.error(messageError);
            updateStatus(data, ResultSinicImportFile.Status.FAILED_IMPORT);
        }

    }

    private void updateStatus(Ili2pgImportSinicDto data, ResultSinicImportFile.Status status) {

        log.info("SENDING STATUS UPDATE ");

        ResultSinicImportFile result = new ResultSinicImportFile();
        result.setResult(status);
        result.setPathFile(data.getPathXTF());
        result.setReference(data.getReference());
        result.setCurrentFile(data.getCurrentFile());
        result.setTotalFiles(data.getTotalFiles());

        try {
            rabbitService.sendResultImportSinicFile(result);
        } catch (Exception e) {
            String messageError = String.format("Error enviando el estado importación del archivo SINIC %s : %s",
                    data.getReference(), e.getMessage());
            SCMTracing.sendError(messageError);
            log.error(messageError);
        }

        log.info("STATUS SENT ");
    }

}
