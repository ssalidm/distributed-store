package za.co.pixelly.distributed.store.orderservice.dto;

import za.co.pixelly.distributed.store.orderservice.entity.Order;
import za.co.pixelly.distributed.store.orderservice.entity.OrderStatus;

import java.time.Instant;

public record OrderResponse(
        String id,
        String customerName,
        String productName,
        int quantity,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt

) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getProductName(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
