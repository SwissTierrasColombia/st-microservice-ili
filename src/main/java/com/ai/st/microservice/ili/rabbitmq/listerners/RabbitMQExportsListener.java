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

import com.ai.st.microservice.ili.business.ConceptBusiness;
import com.ai.st.microservice.ili.business.VersionBusiness;
import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.IliExportResultDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
import com.ai.st.microservice.ili.dto.VersionDataDto;
import com.ai.st.microservice.ili.services.Ili2pgService;
import com.ai.st.microservice.ili.services.RabbitMQSenderService;

@Component
public class RabbitMQExportsListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Ili2pgService ili2pgService;

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

	@RabbitListener(queues = "${st.rabbitmq.queueExports.queue}", concurrency = "${st.rabbitmq.queueExports.concurrency}")
	public void recievedMessage(Ili2pgExportDto data) {

		log.info("export started #" + data.getIntegrationId());

		IliExportResultDto resultDto = new IliExportResultDto();

		try {

			VersionDataDto versionData = versionBusiness.getDataVersion(data.getVersionModel(),
					ConceptBusiness.CONCEPT_OPERATION);
			if (versionData instanceof VersionDataDto) {

				IntegrationStatDto stats = null;
				if (data.getWithStats()) {
					stats = ili2pgService.getIntegrationStats(data.getDatabaseHost(), data.getDatabasePort(),
							data.getDatabaseName(), data.getDatabaseUsername(), data.getDatabasePassword(),
							data.getDatabaseSchema(), data.getVersionModel());
				}

				String tmpDirectoryPrefix = temporalDirectoryPrefix;
				Path tmpDirectory = Files.createTempDirectory(Paths.get(uploadedFiles), tmpDirectoryPrefix);

				String logExport = Paths.get(tmpDirectory.toString(), "export.log").toString();

				Boolean result = ili2pgService.exportToXtf(data.getPathFileXTF(), logExport, versionData.getUrl(),
						srsDefault, versionData.getModels(), data.getDatabaseHost(), data.getDatabasePort(),
						data.getDatabaseName(), data.getDatabaseSchema(), data.getDatabaseUsername(),
						data.getDatabasePassword());

				resultDto.setStatus(result);
				resultDto.setPathFile(data.getPathFileXTF());
				resultDto.setStats(stats);
				resultDto.setModelVersion(data.getVersionModel());
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
