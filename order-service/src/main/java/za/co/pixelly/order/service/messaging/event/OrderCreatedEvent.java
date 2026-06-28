package za.co.pixelly.order.service.messaging.event;

import org.slf4j.MDC;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.entity.OrderStatus;

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
        OrderStatus status,
        UUID reservationId,
        Instant createdAt,
        String correlationId
) {
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getCustomerName(),
                order.getProductId(),
                order.getSku(),
                order.getProductName(),
                order.getUnitPrice(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getStockReservationId(),
                order.getCreatedAt(),
                MDC.get("correlationId")
        );

    }
}
