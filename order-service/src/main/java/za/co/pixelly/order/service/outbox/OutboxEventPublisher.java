package za.co.pixelly.order.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.pixelly.order.service.messaging.event.OrderCreatedEvent;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventPublisher.class);
    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${outbox.publisher.max-retry-attempts}")
    private int maxRetryAttempts;

    @Transactional
    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents =
                outboxEventRepository.findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        LOGGER.info(":::: Publishing {} pending outbox event(s) ::::", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEvent event) {
        try {
            OrderCreatedEvent payload = objectMapper.readValue(
                    event.getPayload(),
                    OrderCreatedEvent.class
            );

            rabbitTemplate.convertAndSend(
                    event.getExchangeName(),
                    event.getRoutingKey(),
                    payload
            );

            event.setStatus(OutboxEventStatus.PUBLISHED);
            event.setPublishedAt(Instant.now());
            event.setLastError(null);

            outboxEventRepository.save(event);

            LOGGER.info(
                    ":::: Published outbox event. eventId={}, eventType={}, aggregateId={} ::::",
                    event.getEventId(),
                    event.getEventType(),
                    event.getAggregateId()
            );
        } catch (Exception ex) {
            int newRetryCount = event.getRetryCount() + 1;

            event.setRetryCount(newRetryCount);
            event.setLastError(ex.getMessage());

            if (newRetryCount >= maxRetryAttempts) {
                event.setStatus(OutboxEventStatus.FAILED);

                LOGGER.error(
                        ":::: Outbox event permanently failed. eventId={}, eventType={}, aggregateId={}, retryCount={}, error={} ::::",
                        event.getEventId(),
                        event.getEventType(),
                        event.getAggregateId(),
                        newRetryCount,
                        ex.getMessage()
                );
            } else {
                LOGGER.warn(
                        ":::: Failed to publish outbox event. eventId={}, retryCount={}, maxRetryAttempts={}, error={} ::::",
                        event.getEventId(),
                        newRetryCount,
                        maxRetryAttempts,
                        ex.getMessage()
                );
            }

            outboxEventRepository.save(event);
        }
    }
}
