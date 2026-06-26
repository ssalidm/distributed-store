package za.co.pixelly.order.service.outbox;

import java.time.Instant;
import java.util.UUID;

public record OutboxEventResponse(
        UUID id,
        UUID eventId,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        String exchangeName,
        String routingKey,
        OutboxEventStatus status,
        Integer retryCount,
        String lastError,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static OutboxEventResponse from(OutboxEvent event) {
        return new OutboxEventResponse(
                event.getId(),
                event.getEventId(),
                event.getEventType(),
                event.getAggregateType(),
                event.getAggregateId(),
                event.getExchangeName(),
                event.getRoutingKey(),
                event.getStatus(),
                event.getRetryCount(),
                event.getLastError(),
                event.getPublishedAt(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}