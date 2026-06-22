package za.co.pixelly.notification.service.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import za.co.pixelly.notification.service.messaging.event.OrderCreatedEvent;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${messaging.notification.dead-letter-exchange}")
    private String deadLetterExchange;

    @Value("${messaging.notification.dead-letter-routing-key}")
    private String deadLetterRoutingKey;

    @Value("${messaging.notification.max-retry-attempts}")
    private int maxRetryAttempts;

    @RabbitListener(queues = "${messaging.order.queue}")
    public void handleOrderCreated(OrderCreatedEvent event, Message message) {
        putCorrelationId(event);

        try {
            LOGGER.info(":::: Received OrderCreated event for orderId={} ::::", event.orderId());

            simulateProcessNotificationFailure();

            processNotification(event);

        } catch (Exception e) {
            int retryCount = getRetryCount(message);

            LOGGER.warn(
                    "Failed to process notifications for orderId={}. retryCount={}, maxRetryAttempts={}, error={}",
                    event.orderId(),
                    retryCount,
                    maxRetryAttempts,
                    e.getMessage()
            );

            if (retryCount >= maxRetryAttempts - 1) {
                LOGGER.error(
                        "Max retry attempts reached for orderId={}. Sending message to DLQ.",
                        event.orderId()
                );

                rabbitTemplate.convertAndSend(deadLetterExchange, deadLetterRoutingKey, event);

                /*
                 * Important:
                 * We do NOT rethrow here.
                 * Returning normally tells RabbitMQ:
                 * "This message has been handled."
                 *
                 * Otherwise, it would go back to the retry queue again.
                 */
                return;
            }

            /*
             * Rethrow so Spring AMQP rejects the message.
             * Since default-requeue-rejected=false,
             * RabbitMQ dead-letters it to the retry queue.
             */
            throw e;

        } finally {
            MDC.remove("correlationId");
        }
    }

    private void simulateProcessNotificationFailure() {
        /*
         * Temporarily simulate failure while testing.
         * Later remove this throw.
         */
        throw new RuntimeException("Simulated notification failure");
    }

    private void processNotification(OrderCreatedEvent event) {
        LOGGER.info(
                "🛒 Notification sent for orderId={}, customerName={}, productName={}, unitPrice={}, quantity={}, totalAmount={}",
                event.orderId(),
                event.customerName(),
                event.productName(),
                event.unitPrice(),
                event.quantity(),
                event.totalAmount()
        );
    }

    private int getRetryCount(Message message) {
        List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();

        if (xDeathHeader == null || xDeathHeader.isEmpty()) {
            return 0;
        }

        return xDeathHeader.stream()
                .filter(entry -> "notification.order-created.queue".equals(entry.get("queue")))
                .findFirst()
                .map(entry -> ((Long) entry.get("count")).intValue())
                .orElse(0);
    }

    private void putCorrelationId(OrderCreatedEvent event) {
        if (event.correlationId() != null && !event.correlationId().isBlank()) {
            MDC.put("correlationId", event.correlationId());
        }
    }
}