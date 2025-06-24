package com.dementor.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NotificationRabbitMqConfig {
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.key";

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         StompRabbitMqBrokerConfig stompRabbitMqBrokerConfig) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(stompRabbitMqBrokerConfig.messageConverter());

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message confirmed : {}", correlationData);
            } else {
                log.error("Message nack-ed : {}, cause: {}", correlationData, cause);
            }
        });
        return rabbitTemplate;
    }
}
