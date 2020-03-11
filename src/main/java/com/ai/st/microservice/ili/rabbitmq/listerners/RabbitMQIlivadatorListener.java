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
import com.ai.st.microservice.ili.dto.IlivalidatorBackgroundDto;
import com.ai.st.microservice.ili.dto.ValidationDto;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.services.IlivalidatorService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;
import com.ai.st.microservice.ili.services.ZipService;

@Component
public class RabbitMQIlivadatorListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${iliProcesses.temporalDirectoryPrefix}")
	private String temporalDirectoryPrefix;

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

	@Value("${iliProcesses.srs}")
	private String srsDefault;

	@Autowired
	private IlivalidatorService ilivalidatorService;

	@Autowired
	private RabbitMQSenderService rabbitService;

	@Autowired
	private ZipService zipService;

	@Autowired
	private VersionBusiness versionBusiness;

	@RabbitListener(queues = "${st.rabbitmq.queueIlivalidator.queue}", concurrency = "${st.rabbitmq.queueIlivalidator.concurrency}")
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
}
