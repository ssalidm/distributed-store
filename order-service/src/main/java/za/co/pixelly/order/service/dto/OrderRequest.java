package za.co.pixelly.order.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderRequest(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotNull(message = "Product id is required")
        UUID productId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {}
