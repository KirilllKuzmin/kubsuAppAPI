package com.kubsu.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String QUEUE_REPORT = "js.report.notify";
    private static final String TOPIC_EXCHANGE_REPORT = "js.report.notify.exchange";
    private static final String ROUTING_KEY_REPORT = "js.key.report";

    //Нам нужно, чтобы spring создал все необходимое для кролика
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Bean
    public TopicExchange reportExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_REPORT);
    }

    @Bean
    public Queue queueReport() {
        return new Queue(QUEUE_REPORT);
    }

    public Binding reportBinding() {
        return BindingBuilder
                .bind(queueReport())
                .to(reportExchange())
                .with(ROUTING_KEY_REPORT);
    }

}
