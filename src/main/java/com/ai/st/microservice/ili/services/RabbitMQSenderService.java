package com.ai.st.microservice.ili.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IliExportResultDto;
import com.ai.st.microservice.ili.dto.IliProcessQueueDto;
import com.ai.st.microservice.ili.dto.IlivalidatorBackgroundDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
import com.ai.st.microservice.ili.dto.ResultImportDto;
import com.ai.st.microservice.ili.dto.ValidationDto;

@Service
public class RabbitMQSenderService {

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Value("${st.rabbitmq.queueIntegrations.exchange}")
	public String exchangeIntegrationsName;

	@Value("${st.rabbitmq.queueIntegrations.routingkey}")
	public String routingkeyIntegrationsName;

	@Value("${st.rabbitmq.queueUpdateIntegration.exchange}")
	public String exchangeUpdateIntegrationsName;

	@Value("${st.rabbitmq.queueUpdateIntegration.routingkey}")
	public String routingkeyUpdateIntegrationsName;

	@Value("${st.rabbitmq.queueExports.exchange}")
	public String exchangeExportsName;

	@Value("${st.rabbitmq.queueExports.routingkey}")
	public String routingkeyExportsName;

	@Value("${st.rabbitmq.queueUpdateExport.exchange}")
	public String exchangeUpdateExportName;

	@Value("${st.rabbitmq.queueUpdateExport.routingkey}")
	public String routingkeyUpdateExportName;

	@Value("${st.rabbitmq.queueIlivalidator.exchange}")
	public String exchangeIlivalidatorName;

	@Value("${st.rabbitmq.queueIlivalidator.routingkey}")
	public String routingkeyIlivalidatorName;

	@Value("${st.rabbitmq.queueUpdateStateSupply.exchange}")
	public String exchangeUpdateStateSupplyName;

	@Value("${st.rabbitmq.queueUpdateStateSupply.routingkey}")
	public String routingkeyUpdateStateSupplyName;

	@Value("${st.rabbitmq.queueIli.exchange}")
	public String exchangeIliName;

	@Value("${st.rabbitmq.queueIli.routingkey}")
	public String routingkeyIliName;

	@Value("${st.rabbitmq.queueResultImport.exchange}")
	public String exchangeResultImportName;

	@Value("${st.rabbitmq.queueResultImport.routingkey}")
	public String routingkeyResultImportName;

	public void sendDataToIntegrate(Ili2pgIntegrationCadastreRegistrationWithoutFilesDto data) {
		rabbitTemplate.convertAndSend(exchangeIntegrationsName, routingkeyIntegrationsName, data);
	}

	public void sendStats(IntegrationStatDto integrationStats) {
		rabbitTemplate.convertSendAndReceive(exchangeUpdateIntegrationsName, routingkeyUpdateIntegrationsName,
				integrationStats);
	}

	public void sendDataToExport(Ili2pgExportDto data) {
		rabbitTemplate.convertAndSend(exchangeExportsName, routingkeyExportsName, data);
	}

	public void sendResultExport(IliExportResultDto data) {
		rabbitTemplate.convertAndSend(exchangeUpdateExportName, routingkeyUpdateExportName, data);
	}

	public void sendDataToValidator(IlivalidatorBackgroundDto data) {
		rabbitTemplate.convertAndSend(exchangeIlivalidatorName, routingkeyIlivalidatorName, data);
	}

	public void sendStatsValidation(ValidationDto data) {
		rabbitTemplate.convertAndSend(exchangeUpdateStateSupplyName, routingkeyUpdateStateSupplyName, data);
	}

	public void sendDataToIliProcess(IliProcessQueueDto data) {
		rabbitTemplate.convertAndSend(exchangeIliName, routingkeyIliName, data);
	}

	public void sendResultToImport(ResultImportDto data) {
		rabbitTemplate.convertAndSend(exchangeResultImportName, routingkeyResultImportName, data);
	}

}
