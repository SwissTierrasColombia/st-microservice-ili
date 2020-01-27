package com.ai.st.microservice.ili.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.dto.Ili2pgExportDto;
import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
import com.ai.st.microservice.ili.dto.IliExportResultDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;

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

}
