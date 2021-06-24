package com.ai.st.microservice.ili.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.ili.dto.IliExportResultDto;
import com.ai.st.microservice.ili.dto.IliProcessQueueDto;
import com.ai.st.microservice.ili.dto.IntegrationStatDto;
import com.ai.st.microservice.ili.dto.ResultExportDto;
import com.ai.st.microservice.ili.dto.ResultImportDto;
import com.ai.st.microservice.ili.dto.ValidationDto;

@Service
public class RabbitMQSenderService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${st.rabbitmq.queueUpdateIntegration.exchange}")
    public String exchangeUpdateIntegrationsName;

    @Value("${st.rabbitmq.queueUpdateIntegration.routingkey}")
    public String routingkeyUpdateIntegrationsName;

    @Value("${st.rabbitmq.queueUpdateExport.exchange}")
    public String exchangeUpdateExportName;

    @Value("${st.rabbitmq.queueUpdateExport.routingkey}")
    public String routingkeyUpdateExportName;

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

    @Value("${st.rabbitmq.queueResultExport.exchange}")
    public String exchangeResultExportName;

    @Value("${st.rabbitmq.queueResultExport.routingkey}")
    public String routingkeyResultExportName;

    @Value("${st.rabbitmq.queueResultValidationProducts.exchange}")
    public String exchangeResultValidationName;

    @Value("${st.rabbitmq.queueResultValidationProducts.routingkey}")
    public String routingKeyResultValidationName;


    public void sendStats(IntegrationStatDto integrationStats) {
        rabbitTemplate.convertSendAndReceive(exchangeUpdateIntegrationsName, routingkeyUpdateIntegrationsName,
                integrationStats);
    }

    public void sendResultExport(IliExportResultDto data) {
        rabbitTemplate.convertAndSend(exchangeUpdateExportName, routingkeyUpdateExportName, data);
    }

    public void sendStatsValidationQueueSupplies(ValidationDto data) {
        rabbitTemplate.convertAndSend(exchangeUpdateStateSupplyName, routingkeyUpdateStateSupplyName, data);
    }

    public void sendStatsValidationQueueProducts(ValidationDto data) {
        rabbitTemplate.convertAndSend(exchangeResultValidationName, routingKeyResultValidationName, data);
    }

    public void sendDataToIliProcess(IliProcessQueueDto data) {
        rabbitTemplate.convertAndSend(exchangeIliName, routingkeyIliName, data);
    }

    public void sendResultToImport(ResultImportDto data) {
        rabbitTemplate.convertAndSend(exchangeResultImportName, routingkeyResultImportName, data);
    }

    public void sendResultToExport(ResultExportDto data) {
        rabbitTemplate.convertAndSend(exchangeResultExportName, routingkeyResultExportName, data);
    }

}
