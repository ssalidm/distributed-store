package za.co.pixelly.order.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import za.co.pixelly.order.service.client.dto.ProductResponse;
import za.co.pixelly.order.service.client.dto.ProductServiceApiResponse;
import za.co.pixelly.order.service.client.dto.StockAdjustmentRequest;
import za.co.pixelly.order.service.exception.InsufficientStockException;
import za.co.pixelly.order.service.exception.ProductNotFoundException;
import za.co.pixelly.order.service.exception.ProductServiceException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductClient.class);
    private final RestClient productRestClient;

    @CircuitBreaker(name = "productService")
    @Retry(name = "productService")
    public ProductResponse getProduct(UUID productId) {
        LOGGER.info("➡️ Calling Product Service for productId={}", productId);

        try {
            String correlationId = MDC.get("correlationId");
            LOGGER.info("Forwarding correlationId={} to Product Service", correlationId);
            ProductServiceApiResponse<ProductResponse> response = productRestClient.get()
                    .uri("/api/products/id/{productId}", productId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || !response.success() || response.result() == null) {
                throw new ProductServiceException("Failed to retrieve product from Product Service");
            }

            return response.result();

        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException("Product not found");
            }

            throw new ProductServiceException(
                    "Product Service returned an unexpected error"
            );

        } catch (ResourceAccessException e) {
            throw new ProductServiceException(
                    "Product Service is currently unreachable or timed out"
            );
        }
    }

    public ProductResponse reserveStock(UUID productId, UUID reservationId, Integer quantity) {
        try {
            LOGGER.info(":::: Reserving stock for productId={}, reservationId={}, quantity={}",
                    productId,
                    reservationId,
                    quantity
            );

            ProductServiceApiResponse<ProductResponse> response = productRestClient
                    .patch()
                    .uri("/api/products/{productId}/stock/reserve", productId)
                    .body(new StockAdjustmentRequest(reservationId, quantity))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.result() == null) {
                throw new ProductServiceException("Product Service returned an empty reserve stock response");
            }

            return response.result();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException("Product not found");
            }

            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new InsufficientStockException("Insufficient stock available");
            }

            throw new ProductServiceException(("Product Service returned an unexpected error while reserving stock"));
        } catch (ResourceAccessException ex) {
            throw new ProductServiceException("Product service is currently unreachable while reserving stock");
        }
    }

    public void releaseStock(UUID productId, UUID reservationId, Integer quantity) {
        try {
            LOGGER.info(":::: Releasing stock for productId={}, reservationId={}, quantity={}",
                    productId,
                    reservationId,
                    quantity);

            productRestClient
                    .patch()
                    .uri("/api/products/{productId}/stock/release", productId)
                    .body(new StockAdjustmentRequest(reservationId, quantity))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw new ProductServiceException("Product Service returned an unexpected error while releasing stock");
        } catch (ResourceAccessException ex) {
            throw new ProductServiceException("Product Service is currently unreachable while releasing stock");
        }
    }
}
