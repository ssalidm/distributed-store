package za.co.pixelly.distributed.store.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import za.co.pixelly.distributed.store.orderservice.entity.OrderStatus;

public record OrderStatusRequest(
        @NotNull
        OrderStatus status // PENDING, PROCESSING, COMPLETED, CANCELLED
) {}
