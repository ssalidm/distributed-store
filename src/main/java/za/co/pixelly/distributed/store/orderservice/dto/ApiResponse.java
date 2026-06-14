package za.co.pixelly.distributed.store.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Standard response envelope for every API endpoint.
 *
 * @JsonInclude(NON_NULL) — Fields that are null are omitted from the JSON.
 * This keeps success responses clean (no "errors": null)
 * and error responses clean (no "result": null).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T result,
        Map<String, String> errors,
        String path,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T result, String message, int status) {
        return new ApiResponse<>(
                true,
                status,
                message,
                result,
                null,
                null,
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(null, message, HttpStatus.OK.value());
    }

    public static ApiResponse<Void> error(String message, int status, String path) {
        return new ApiResponse<>(
                false,
                status,
                message,
                null,
                null,
                path,
                Instant.now()
        );
    }

    public static ApiResponse<Void> validationError(
            Map<String, String> errors, String path) {
        return new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                null,
                errors,
                path,
                Instant.now()
        );
    }
}
