package com.ai.st.microservice.ili.rabbitmq;

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

	@Value("${st.rabbitmq.queueIntegrations.queue}")
	public String queueIntegrationsName;

	@Value("${st.rabbitmq.queueIntegrations.exchange}")
	public String exchangeIntegrationsName;

	@Value("${st.rabbitmq.queueIntegrations.routingkey}")
	public String routingkeyIntegrationsName;

	@Value("${st.rabbitmq.queueUpdateIntegration.queue}")
	public String queueUpdateIntegrationsName;

	@Value("${st.rabbitmq.queueUpdateIntegration.exchange}")
	public String exchangeUpdateIntegrationsName;

	@Value("${st.rabbitmq.queueUpdateIntegration.routingkey}")
	public String routingkeyUpdateIntegrationsName;

	@Value("${st.rabbitmq.queueExports.queue}")
	public String queueExportsName;

	@Value("${st.rabbitmq.queueExports.exchange}")
	public String exchangeExportsName;

	@Value("${st.rabbitmq.queueExports.routingkey}")
	public String routingkeyExportsName;

	@Bean
	public Queue queueIntegrations() {
		return new Queue(queueIntegrationsName, false);
	}

	@Bean
	public DirectExchange exchangeIntegrations() {
		return new DirectExchange(exchangeIntegrationsName);
	}

	@Bean
	public Binding bindingQueueIntegrations() {
		return BindingBuilder.bind(queueIntegrations()).to(exchangeIntegrations()).with(routingkeyIntegrationsName);
	}

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

	@Bean
	public Queue queueExports() {
		return new Queue(queueExportsName, false);
	}

	@Bean
	public DirectExchange exchangeExports() {
		return new DirectExchange(exchangeExportsName);
	}

	@Bean
	public Binding bindingQueueExports() {
		return BindingBuilder.bind(queueExports()).to(exchangeExports()).with(routingkeyExportsName);
	}

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
