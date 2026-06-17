package za.co.pixelly.product.service.service;

import za.co.pixelly.product.service.dto.ProductRequest;
import za.co.pixelly.product.service.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    List<ProductResponse> getProducts();

    ProductResponse getProduct(UUID productId);

    void deleteProduct(UUID productId);
}
