package com.ai.st.microservice.ili.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.dto.Ili2pgIntegrationCadastreRegistrationWithoutFilesDto;
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

	public void sendDataToIntegrate(Ili2pgIntegrationCadastreRegistrationWithoutFilesDto data) {
		rabbitTemplate.convertAndSend(exchangeIntegrationsName, routingkeyIntegrationsName, data);
	}

	public void sendStats(IntegrationStatDto integrationStats) {
		rabbitTemplate.convertSendAndReceive(exchangeUpdateIntegrationsName, routingkeyUpdateIntegrationsName,
				integrationStats);
	}

}