package za.co.pixelly.product.service.service;

import za.co.pixelly.product.service.dto.ProductCreateRequest;
import za.co.pixelly.product.service.dto.ProductResponse;
import za.co.pixelly.product.service.dto.ProductUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductCreateRequest request);

    List<ProductResponse> getProducts();

    ProductResponse getProductById(UUID productId);

    ProductResponse getProductBySku(String sku);

    ProductResponse updateProduct(UUID productId, ProductUpdateRequest request);

    void deleteProduct(UUID productId);
}
