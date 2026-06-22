package za.co.pixelly.order.service.messaging.event;

import org.slf4j.MDC;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String customerName,
        UUID productId,
        String productSku,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt,
        String correlationId
) {
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getCustomerName(),
                order.getProductId(),
                order.getSku(),
                order.getProductName(),
                order.getUnitPrice(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                MDC.get("correlationId")
        );

    }
}
