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
}
