package com.ai.st.microservice.ili.rabbitmq.listerners;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
import com.ai.st.microservice.ili.services.Ili2pgService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;
import com.ai.st.microservice.ili.services.ZipService;

@Component
public class RabbitMQIntegrationListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ZipService zipService;

	@Autowired
	private Ili2pgService ili2pgService;

	@Autowired
	private RabbitMQSenderService rabbitService;

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

	@RabbitListener(queues = "${st.rabbitmq.queueIntegrations.queue}")
	public void recievedMessage(Ili2pgIntegrationCadastreRegistrationWithoutFilesDto data) {

		System.out.println("integration started ");

		try {

			String tmpDirectoryPrefix = temporalDirectoryPrefix;
			Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

			List<String> pathsCadastre = zipService.unzip(data.getCadastrePathXTF(), new File(tmpDirectory.toString()));
			String pathFileCadastre = tmpDirectory.toString() + File.separator + pathsCadastre.get(0);
			System.out.println("PATH: " + pathFileCadastre);

			List<String> pathsRegistration = zipService.unzip(data.getRegistrationPathXTF(),
					new File(tmpDirectory.toString()));
			String pathFileRegistration = tmpDirectory.toString() + File.separator + pathsRegistration.get(0);
			System.out.println("PATH: " + pathFileRegistration);

			String cadastreLogFileSchemaImport = tmpDirectory.toString() + File.separator
					+ "cadastre_schema_import.log";
			String cadastreLogFileImport = tmpDirectory.toString() + File.separator + "cadastre_import.log";

			String registrationLogFileSchemaImport = tmpDirectory.toString() + File.separator
					+ "registration_schema_import.log";
			String registrationLogFileImport = tmpDirectory.toString() + File.separator + "registration_import.log";

			IntegrationStatDto integrationStatDto = ili2pgService.integration(pathFileCadastre,
					cadastreLogFileSchemaImport, cadastreLogFileImport, pathFileRegistration,
					registrationLogFileSchemaImport, registrationLogFileImport, iliDirectory, srsDefault, modelsDefault,
					data.getDatabaseHost(), data.getDatabasePort(), data.getDatabaseName(), data.getDatabaseSchema(),
					data.getDatabaseUsername(), data.getDatabasePassword());

			System.out.println("stats: " + integrationStatDto.getPercentage());

			rabbitService.sendStats(integrationStatDto);

		} catch (Exception e) {
			log.error("Integration error: " + e.getMessage());
		}

	}

}
