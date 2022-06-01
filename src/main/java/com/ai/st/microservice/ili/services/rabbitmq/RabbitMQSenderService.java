package com.ai.st.microservice.ili.services.rabbitmq;

import com.ai.st.microservice.ili.dto.*;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${st.rabbitmq.queueResultValidationSinicFiles.exchange}")
    public String exchangeResultValidationSinicFilesName;

    @Value("${st.rabbitmq.queueResultValidationSinicFiles.routingkey}")
    public String routingKeyResultValidationSinicFilesName;

    @Value("${st.rabbitmq.queueResultProcessSinicFiles.exchange}")
    public String queueResultProcessSinicFilesExchange;

    @Value("${st.rabbitmq.queueResultProcessSinicFiles.routingkey}")
    public String queueResultProcessSinicFilesRoutingKey;

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

    public void sendStatsValidationQueueSinicFiles(ValidationDto data) {
        rabbitTemplate.convertAndSend(exchangeResultValidationSinicFilesName, routingKeyResultValidationSinicFilesName,
                data);
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

    public void sendResultImportSinicFile(ResultSinicImportFile data) {
        rabbitTemplate.convertAndSend(queueResultProcessSinicFilesExchange, queueResultProcessSinicFilesRoutingKey,
                data);
    }

}
