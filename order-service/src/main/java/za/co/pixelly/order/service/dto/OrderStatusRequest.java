package za.co.pixelly.order.service.dto;

import jakarta.validation.constraints.NotNull;
import za.co.pixelly.order.service.entity.OrderStatus;

public record OrderStatusRequest(
        @NotNull
        OrderStatus status // PENDING, PROCESSING, COMPLETED, CANCELLED
) {}
