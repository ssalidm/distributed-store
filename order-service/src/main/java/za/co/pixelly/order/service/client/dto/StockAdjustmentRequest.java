package za.co.pixelly.order.service.client.dto;

import java.util.UUID;

public record StockAdjustmentRequest(
        UUID reservationId,
        Integer quantity
) {
}