package com.ai.st.microservice.ili.rabbitmq.listerners;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.business.ConceptBusiness;
import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IliExportResultDto;
import com.ai.st.microservice.ili.dto.IliProcessQueueDto;
import com.ai.st.microservice.ili.dto.IlivalidatorBackgroundDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
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

	@Value("${iliProcesses.temporalDirectoryPrefix}")
	private String temporalDirectoryPrefix;

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

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

	}

	public void ilivalidator(IlivalidatorBackgroundDto data) {

		log.info("validation started #" + data.getRequestId());

		Boolean validation = false;

		try {

			VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(),
					ConceptBusiness.CONCEPT_OPERATION);
			if (versionData instanceof VersionDataDto) {

				Path path = Paths.get(data.getPathFile());
				String fileName = path.getFileName().toString();
				String fileExtension = FilenameUtils.getExtension(fileName);

				String pathFileXTF = "";

				File unzipFile = null;

				if (fileExtension.equalsIgnoreCase("zip")) {

					Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), temporalDirectoryPrefix);

					List<String> paths = zipService.unzip(data.getPathFile(), new File(tmpDirectory.toString()));
					pathFileXTF = tmpDirectory.toString() + File.separator + paths.get(0);

					unzipFile = tmpDirectory.toFile();

				} else if (fileExtension.equalsIgnoreCase("xtf")) {
					pathFileXTF = data.getPathFile();
				}

				if (pathFileXTF.isBlank() || pathFileXTF.isEmpty()) {
					log.error("there is not file xtf.");
				} else {

					Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), temporalDirectoryPrefix);

					String logFileValidation = Paths.get(tmpDirectory.toString(), "ilivalidator.log").toString();
					String logFileValidationXTF = Paths.get(tmpDirectory.toString(), "ilivalidator.xtf").toString();

					validation = ilivalidatorService.validate(pathFileXTF, versionData.getUrl(),
							versionData.getModels(), null, logFileValidation, logFileValidationXTF, null);
					log.info("validation successful with result: " + validation);

					try {
						FileUtils.deleteDirectory(tmpDirectory.toFile());
						if (unzipFile != null) {
							FileUtils.deleteDirectory(unzipFile);
						}
					} catch (Exception e) {
						log.error("It has not been possible delete the directory: " + e.getMessage());
					}

				}

			}

		} catch (Exception e) {
			log.error("validation failed # " + data.getRequestId() + " : " + e.getMessage());
		}

		ValidationDto validationDto = new ValidationDto();
		validationDto.setIsValid(validation);
		validationDto.setRequestId(data.getRequestId());
		validationDto.setSupplyRequestedId(data.getSupplyRequestedId());
		validationDto.setFilenameTemporal(data.getFilenameTemporal());
		validationDto.setUserCode(data.getUserCode());
		validationDto.setObservations(data.getObservations());

		rabbitService.sendStatsValidation(validationDto);
	}

	public void integration(Ili2pgIntegrationCadastreRegistrationWithoutFilesDto data) {

		log.info("integration started #" + data.getIntegrationId());

		IntegrationStatDto integrationStatDto = null;

		try {

			VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(),
					ConceptBusiness.CONCEPT_INTEGRATION);
			if (versionData instanceof VersionDataDto) {

				Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), temporalDirectoryPrefix);

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

				versionData.getQueries();

				integrationStatDto = ili2pgService.integration(pathFileCadastre, cadastreLogFileSchemaImport,
						cadastreLogFileImport, pathFileRegistration, registrationLogFileSchemaImport,
						registrationLogFileImport, versionData.getUrl(), srsDefault, versionData.getModels(),
						data.getDatabaseHost(), data.getDatabasePort(), data.getDatabaseName(),
						data.getDatabaseSchema(), data.getDatabaseUsername(), data.getDatabasePassword(),
						data.getVersionModel());

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
			if (versionData instanceof VersionDataDto) {

				IntegrationStatDto stats = null;
				if (data.getWithStats()) {
					stats = ili2pgService.getIntegrationStats(data.getDatabaseHost(), data.getDatabasePort(),
							data.getDatabaseName(), data.getDatabaseUsername(), data.getDatabasePassword(),
							data.getDatabaseSchema(), data.getVersionModel());
				}

				Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), temporalDirectoryPrefix);

				String logExport = Paths.get(tmpDirectory.toString(), "export.log").toString();

				Boolean result = ili2pgService.exportToXtf(data.getPathFileXTF(), logExport, versionData.getUrl(),
						srsDefault, versionData.getModels(), data.getDatabaseHost(), data.getDatabasePort(),
						data.getDatabaseName(), data.getDatabaseSchema(), data.getDatabaseUsername(),
						data.getDatabasePassword());

				resultDto.setStatus(result);
				resultDto.setPathFile(data.getPathFileXTF());
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

}
