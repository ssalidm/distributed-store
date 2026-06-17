package za.co.pixelly.order.service.client.dto;

import java.time.Instant;
import java.util.Map;

public record ProductServiceApiResponse<T>(
        boolean success,
        int status,
        String message,
        T result,
        Map<String, String> errors,
        String path,
        Instant timestamp
) {
}
