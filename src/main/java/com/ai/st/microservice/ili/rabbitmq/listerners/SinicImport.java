package com.ai.st.microservice.ili.rabbitmq.listerners;

import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.Ili2pgImportSinicDto;
import com.ai.st.microservice.ili.dto.ResultSinicImportFile;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.services.Ili2pgService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;
import com.ai.st.microservice.ili.services.ZipService;
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

    public SinicImport(
            Ili2pgService ili2pgService,
            ZipService zipService,
            RabbitMQSenderService rabbitService,
            VersionBusiness versionBusiness,
            String stTemporalDirectory,
            String srsDefault) {
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

                log.warn("AQUII -1: " + data.getPathXTF());
                log.warn("AQUII -1: " + stTemporalDirectory);

                String nameDirectory = "ili_sinic_process_import_" + RandomStringUtils.random(8, false, true);
                log.warn("AQUII -1: " + nameDirectory);
                Path tmpDirectory = Files.createTempDirectory(Paths.get(stTemporalDirectory), nameDirectory);

                log.warn("AQUII 0: ");

                List<String> paths = zipService.unzip(data.getPathXTF(), new File(tmpDirectory.toString()));
                String pathFileXTF = tmpDirectory + File.separator + paths.get(0);

                log.warn("AQUII 1: ");

                String logFileSchemaImport = tmpDirectory + File.separator + "schema_sinic_import.log";
                String logFileImport = tmpDirectory + File.separator + "sinic_import.log";

                String models = "Submodelo_Insumos_Gestor_Catastral_V1_0;Submodelo_Insumos_SNR_V1_0;Submodelo_Integracion_Insumos_V1_0;";
//                String models = versionData.getModels();
                log.warn("AQUII JHON: " + models + " version: " + versionData.getUrl());

                boolean importValid = ili2pgService.import2pg(pathFileXTF, logFileSchemaImport, logFileImport,
                        versionData.getUrl(), srsDefault, models, data.getDatabaseHost(),
                        data.getDatabasePort(), data.getDatabaseName(), data.getDatabaseSchema(),
                        data.getDatabaseUsername(), data.getDatabasePassword());

                log.warn("AQUII 2: ");

                try {
                    FileUtils.deleteDirectory(tmpDirectory.toFile());
                } catch (Exception e) {
                    log.error("It has not been possible delete the directory: " + e.getMessage());
                }

                log.info("SINIC IMPORT FINISHED WITH RESULT: " + importValid);
                ResultSinicImportFile.Status status = (importValid) ? ResultSinicImportFile.Status.SUCCESS_IMPORT :
                        ResultSinicImportFile.Status.FAILED_IMPORT;
                updateStatus(data, status);

            } else {
                log.error("CONCEPT VERSION NOT FOUND: ");
                updateStatus(data, ResultSinicImportFile.Status.FAILED_IMPORT);
            }

        } catch (Exception e) {
            log.error("IMPORT FAILED WITH ERROR: " + e.getMessage());
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
            log.error("Error sending status sinic import: " + e.getMessage());
        }

        log.info("STATUS SENT ");

    }

}
