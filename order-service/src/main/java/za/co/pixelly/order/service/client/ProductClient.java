package za.co.pixelly.order.service.client;

import lombok.RequiredArgsConstructor;
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

    private final RestClient productRestClient;

    public ProductResponse getProduct(UUID productId) {
        try {
            ProductServiceApiResponse<ProductResponse> response = productRestClient.get()
                    .uri("/api/products/{productId}", productId)
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
                    "Product Service is currently unreachable"
            );
        }
    }
}
