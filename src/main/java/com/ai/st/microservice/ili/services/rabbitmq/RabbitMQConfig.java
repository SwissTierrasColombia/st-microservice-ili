package com.ai.st.microservice.ili.services.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${st.rabbitmq.queueUpdateIntegration.queue}")
    public String queueUpdateIntegrationsName;

    @Value("${st.rabbitmq.queueUpdateIntegration.exchange}")
    public String exchangeUpdateIntegrationsName;

    @Value("${st.rabbitmq.queueUpdateIntegration.routingkey}")
    public String routingkeyUpdateIntegrationsName;

    @Value("${st.rabbitmq.queueUpdateExport.queue}")
    public String queueUpdateExportName;

    @Value("${st.rabbitmq.queueUpdateExport.exchange}")
    public String exchangeUpdateExportName;

    @Value("${st.rabbitmq.queueUpdateExport.routingkey}")
    public String routingkeyUpdateExportName;

    @Value("${st.rabbitmq.queueUpdateStateSupply.queue}")
    public String queueUpdateStateSupplyName;

    @Value("${st.rabbitmq.queueUpdateStateSupply.exchange}")
    public String exchangeUpdateStateSupplyName;

    @Value("${st.rabbitmq.queueUpdateStateSupply.routingkey}")
    public String routingkeyUpdateStateSupplyName;

    @Value("${st.rabbitmq.queueIli.queue}")
    public String queueIliName;

    @Value("${st.rabbitmq.queueIli.exchange}")
    public String exchangeIliName;

    @Value("${st.rabbitmq.queueIli.routingkey}")
    public String routingkeyIliName;

    @Value("${st.rabbitmq.queueResultImport.queue}")
    public String queueResultImportName;

    @Value("${st.rabbitmq.queueResultImport.exchange}")
    public String exchangeResultImportName;

    @Value("${st.rabbitmq.queueResultImport.routingkey}")
    public String routingkeyResultImportName;

    @Value("${st.rabbitmq.queueResultExport.queue}")
    public String queueResultExportName;

    @Value("${st.rabbitmq.queueResultExport.exchange}")
    public String exchangeResultExportName;

    @Value("${st.rabbitmq.queueResultExport.routingkey}")
    public String routingkeyResultExportName;

    // queue update integrations

    @Bean
    public Queue queueUpdateIntegrations() {
        return new Queue(queueUpdateIntegrationsName, false);
    }

    @Bean
    public DirectExchange exchangeUpdateIntegrations() {
        return new DirectExchange(exchangeUpdateIntegrationsName);
    }

    @Bean
    public Binding bindingQueueUpdateIntegrations() {
        return BindingBuilder.bind(queueUpdateIntegrations()).to(exchangeUpdateIntegrations())
                .with(routingkeyUpdateIntegrationsName);
    }

    // queue update exports

    @Bean
    public Queue queueUpdateExports() {
        return new Queue(queueUpdateExportName, false);
    }

    @Bean
    public DirectExchange exchangeUpdateExports() {
        return new DirectExchange(exchangeUpdateExportName);
    }

    @Bean
    public Binding bindingQueueUpdateExports() {
        return BindingBuilder.bind(queueUpdateExports()).to(exchangeUpdateExports()).with(routingkeyUpdateExportName);
    }

    // queue update state supply

    @Bean
    public Queue queueUpdateStateSupply() {
        return new Queue(queueUpdateStateSupplyName, false);
    }

    @Bean
    public DirectExchange exchangeUpdateStateSupply() {
        return new DirectExchange(exchangeUpdateStateSupplyName);
    }

    @Bean
    public Binding bindingQueueUpdateStateSupply() {
        return BindingBuilder.bind(queueUpdateStateSupply()).to(exchangeUpdateStateSupply())
                .with(routingkeyUpdateStateSupplyName);
    }

    // queue ili

    @Bean
    public Queue queueIli() {
        return new Queue(queueIliName, false);
    }

    @Bean
    public DirectExchange exchangeIli() {
        return new DirectExchange(exchangeIliName);
    }

    @Bean
    public Binding bindingQueueIli() {
        return BindingBuilder.bind(queueIli()).to(exchangeIli()).with(routingkeyIliName);
    }

    // queue result imports reference

    @Bean
    public Queue queueResultImport() {
        return new Queue(queueResultImportName, false);
    }

    @Bean
    public DirectExchange exchangeResultImport() {
        return new DirectExchange(exchangeResultImportName);
    }

    @Bean
    public Binding bindingQueueResultImport() {
        return BindingBuilder.bind(queueResultImport()).to(exchangeResultImport()).with(routingkeyResultImportName);
    }

    // queue result exports reference

    @Bean
    public Queue queueResultExport() {
        return new Queue(queueResultExportName, false);
    }

    @Bean
    public DirectExchange exchangeResultExport() {
        return new DirectExchange(exchangeResultExportName);
    }

    @Bean
    public Binding bindingQueueResultExport() {
        return BindingBuilder.bind(queueResultExport()).to(exchangeResultExport()).with(routingkeyResultExportName);
    }

    // queue result validation products

    @Value("${st.rabbitmq.queueResultValidationProducts.queue}")
    public String queueResultValidation;

    @Value("${st.rabbitmq.queueResultValidationProducts.exchange}")
    public String exchangeResultValidation;

    @Value("${st.rabbitmq.queueResultValidationProducts.routingkey}")
    public String routingKeyResultValidation;

    @Bean
    public Queue QueueResultValidation() {
        return new Queue(queueResultValidation, false);
    }

    @Bean
    public DirectExchange exchangeResultValidation() {
        return new DirectExchange(exchangeResultValidation);
    }

    @Bean
    public Binding bindingQueueResultValidation() {
        return BindingBuilder.bind(QueueResultValidation()).to(exchangeResultValidation())
                .with(routingKeyResultValidation);
    }

    // queue result validation sinic files

    @Value("${st.rabbitmq.queueResultValidationSinicFiles.queue}")
    public String queueResultValidationSinicFiles;

    @Value("${st.rabbitmq.queueResultValidationSinicFiles.exchange}")
    public String exchangeResultValidationSinicFiles;

    @Value("${st.rabbitmq.queueResultValidationSinicFiles.routingkey}")
    public String routingKeyResultValidationSinicFiles;

    @Bean
    public Queue QueueResultValidationSinicFiles() {
        return new Queue(queueResultValidationSinicFiles, false);
    }

    @Bean
    public DirectExchange exchangeResultValidationSinicFiles() {
        return new DirectExchange(exchangeResultValidationSinicFiles);
    }

    @Bean
    public Binding bindingQueueResultValidationSinicFiles() {
        return BindingBuilder.bind(QueueResultValidationSinicFiles()).to(exchangeResultValidationSinicFiles())
                .with(routingKeyResultValidationSinicFiles);
    }

    // queue result process sinic files

    @Value("${st.rabbitmq.queueResultProcessSinicFiles.queue}")
    public String queueResultProcessSinicFilesName;

    @Value("${st.rabbitmq.queueResultProcessSinicFiles.exchange}")
    public String queueResultProcessSinicFilesExchange;

    @Value("${st.rabbitmq.queueResultProcessSinicFiles.routingkey}")
    public String queueResultProcessSinicFilesRoutingKey;

    @Bean
    public Queue queueResultProcessSinicFilesName() {
        return new Queue(queueResultProcessSinicFilesName, false);
    }

    @Bean
    public DirectExchange exchangeResultProcessSinicFilesName() {
        return new DirectExchange(queueResultProcessSinicFilesExchange);
    }

    // queue ili sinic large

    @Value("${st.rabbitmq.queueIliSinicLarge.queue}")
    public String queueIliSinicLargeName;

    @Value("${st.rabbitmq.queueIliSinicLarge.exchange}")
    public String exchangeIliSinicLargeName;

    @Value("${st.rabbitmq.queueIliSinicLarge.routingkey}")
    public String routingkeyIliSinicLargeName;

    @Bean
    public Queue queueIliSinicLargeName() {
        return new Queue(queueIliSinicLargeName, false);
    }

    @Bean
    public DirectExchange exchangeIliSinicLargeName() {
        return new DirectExchange(exchangeIliSinicLargeName);
    }

    @Bean
    public Binding bindingIliSinicLargeName() {
        return BindingBuilder.bind(queueIliSinicLargeName()).to(exchangeIliSinicLargeName())
                .with(routingkeyIliSinicLargeName);
    }

    // queue ili sinic small

    @Value("${st.rabbitmq.queueIliSinicSmall.queue}")
    public String queueIliSinicSmallName;

    @Value("${st.rabbitmq.queueIliSinicSmall.exchange}")
    public String exchangeIliSinicSmallName;

    @Value("${st.rabbitmq.queueIliSinicSmall.routingkey}")
    public String routingkeyIliSinicSmallName;

    @Bean
    public Queue queueIliSinicSmallName() {
        return new Queue(queueIliSinicSmallName, false);
    }

    @Bean
    public DirectExchange exchangeIliSinicSmallName() {
        return new DirectExchange(exchangeIliSinicSmallName);
    }

    @Bean
    public Binding bindingIliSinicSmallName() {
        return BindingBuilder.bind(queueIliSinicSmallName()).to(exchangeIliSinicSmallName())
                .with(routingkeyIliSinicSmallName);
    }

    // Setup

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
