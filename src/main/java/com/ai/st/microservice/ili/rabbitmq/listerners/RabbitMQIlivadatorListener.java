package com.ai.st.microservice.ili.rabbitmq.listerners;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.dto.IlivalidatorBackgroundDto;
import com.ai.st.microservice.ili.dto.ValidationDto;
import com.ai.st.microservice.ili.services.IlivalidatorService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;

@Component
public class RabbitMQIlivadatorListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${iliProcesses.temporalDirectoryPrefix}")
	private String temporalDirectoryPrefix;

	@Value("${iliProcesses.uploadedFiles}")
	private String uploadedFiles;

	@Value("${iliProcesses.iliDirectory}")
	private String iliDirectory;

	@Value("${iliProcesses.models}")
	private String modelsDefault;

	@Value("${iliProcesses.srs}")
	private String srsDefault;

	@Autowired
	private IlivalidatorService ilivalidatorService;

	@Autowired
	private RabbitMQSenderService rabbitService;

	@RabbitListener(queues = "${st.rabbitmq.queueIlivalidator.queue}", concurrency = "${st.rabbitmq.queueIlivalidator.concurrency}")
	public void ilivalidator(IlivalidatorBackgroundDto data) {

		log.info("validation started #" + data.getRequestId());

		Boolean validation = false;

		try {

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			String logFileValidation = Paths.get(tmpDirectory.toString(), "ilivalidator.log").toString();
			String logFileValidationXTF = Paths.get(tmpDirectory.toString(), "ilivalidator.xtf").toString();

			validation = ilivalidatorService.validate(data.getPathFile(), iliDirectory, modelsDefault, null,
					logFileValidation, logFileValidationXTF, null);

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
