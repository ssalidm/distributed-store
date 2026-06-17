package za.co.pixelly.product.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.pixelly.product.service.dto.ProductRequest;
import za.co.pixelly.product.service.dto.ProductResponse;
import za.co.pixelly.product.service.entity.Product;
import za.co.pixelly.product.service.exception.ProductNotFoundException;
import za.co.pixelly.product.service.repository.ProductRepository;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product savedProduct = productRepository.saveAndFlush(toEntity(request));
        return ProductResponse.from(savedProduct);
    }

    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream().map(ProductResponse::from).toList();
    }

    @Override
    public ProductResponse getProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                ProductNotFoundException::new);
        return ProductResponse.from(product);
    }

    @Override
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                ProductNotFoundException::new);
        productRepository.delete(product);
    }

    private static Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.name())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();
    }
}
