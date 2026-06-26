package za.co.pixelly.notification.service.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String customerName,
        UUID productId,
        String productSku,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalAmount,
        String status,
        Instant createdAt,
        String correlationId
) {}
