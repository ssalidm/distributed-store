package za.co.pixelly.product.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.pixelly.product.service.dto.ProductCreateRequest;
import za.co.pixelly.product.service.dto.ProductResponse;
import za.co.pixelly.product.service.dto.ProductUpdateRequest;
import za.co.pixelly.product.service.entity.Product;
import za.co.pixelly.product.service.exception.ProductNotFoundException;
import za.co.pixelly.product.service.repository.ProductRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with SKU '" + request.sku() + "' already exists.");
        }
        Product savedProduct = productRepository.saveAndFlush(toEntity(request));
        return ProductResponse.from(savedProduct);
    }

    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream().map(ProductResponse::from).toList();
    }

    @Override
    public ProductResponse getProductById(UUID productId) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        return ProductResponse.from(findProduct(productId));
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findProductBySku(sku).orElseThrow(
                ProductNotFoundException::new
        );
        return ProductResponse.from(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest request) {
        Product product = findProduct(productId);

        boolean skuChanged = !product.getSku().equals(request.sku());
        if (skuChanged && productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with SKU '" + request.sku() + "' already exists.");
        }

        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        Product updatedProduct = productRepository.saveAndFlush(product);
        return ProductResponse.from(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId) {
        productRepository.delete(findProduct(productId));
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId).orElseThrow(
                ProductNotFoundException::new);
    }

    private static Product toEntity(ProductCreateRequest request) {
        return Product.builder()
                .name(request.name())
                .sku(request.sku())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();
    }
}
