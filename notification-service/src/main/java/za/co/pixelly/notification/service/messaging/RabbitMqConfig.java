package za.co.pixelly.notification.service.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public DirectExchange orderExchange(
            @Value("${messaging.order.exchange}") String exchangeName
    ) {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public DirectExchange notificationDeadLetterExchange(
            @Value("${messaging.notification.dead-letter-exchange}") String deadLetterExchangeName
    ) {
        return new DirectExchange(deadLetterExchangeName);
    }

    @Bean
    public Queue orderCreatedQueue(
            @Value("${messaging.order.queue}") String queueName,
            @Value("${messaging.notification.dead-letter-exchange}") String deadLetterExchangeName,
            @Value("${messaging.notification.retry-routing-key}") String retryRoutingKey
    ) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-dead-letter-routing-key", retryRoutingKey)
                .build();
    }

    @Bean
    public Queue orderCreatedRetryQueue(
            @Value("${messaging.notification.retry-queue}") String retryQueueName,
            @Value("${messaging.order.exchange}") String orderExchangeName,
            @Value("${messaging.order.routing-key}") String orderRoutingKey,
            @Value("${messaging.notification.retry-delay-ms}") Integer retryDelayMs
    ) {
        return QueueBuilder.durable(retryQueueName)
                .withArgument("x-message-ttl", retryDelayMs)
                .withArgument("x-dead-letter-exchange", orderExchangeName)
                .withArgument("x-dead-letter-routing-key", orderRoutingKey)
                .build();
    }

    @Bean
    public Queue orderCreatedDeadLetterQueue(
            @Value("${messaging.notification.dead-letter-queue}") String deadLetterQueueName
    ) {
        return QueueBuilder.durable(deadLetterQueueName).build();
    }

    @Bean
    public Binding orderCreatedBinding(
            Queue orderCreatedQueue,
            DirectExchange orderExchange,
            @Value("${messaging.order.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(orderCreatedQueue)
                .to(orderExchange)
                .with(routingKey);
    }

    @Bean
    public Binding orderCreatedRetryBinding(
            Queue orderCreatedRetryQueue,
            DirectExchange notificationDeadLetterExchange,
            @Value("${messaging.notification.retry-routing-key}") String retryRoutingKey
    ) {
        return BindingBuilder
                .bind(orderCreatedRetryQueue)
                .to(notificationDeadLetterExchange)
                .with(retryRoutingKey);
    }

    @Bean
    public Binding orderCreatedDeadLetterBinding(
            Queue orderCreatedDeadLetterQueue,
            DirectExchange notificationDeadLetterExchange,
            @Value("${messaging.notification.dead-letter-routing-key}") String deadLetterRoutingKey
    ) {
        return BindingBuilder
                .bind(orderCreatedDeadLetterQueue)
                .to(notificationDeadLetterExchange)
                .with(deadLetterRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}