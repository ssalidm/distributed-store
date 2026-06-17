package za.co.pixelly.order.service.client.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        Integer stockQuantity,
        Instant createdAt,
        Instant updatedAt
) {
}
