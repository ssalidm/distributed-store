package za.co.pixelly.order.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Product name is required")
        String productName,

        @Min(1)
        int quantity
) {}
