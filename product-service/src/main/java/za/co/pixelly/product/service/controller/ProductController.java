package za.co.pixelly.product.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.pixelly.product.service.dto.ApiResponse;
import za.co.pixelly.product.service.dto.ProductCreateRequest;
import za.co.pixelly.product.service.dto.ProductResponse;
import za.co.pixelly.product.service.dto.ProductUpdateRequest;
import za.co.pixelly.product.service.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        LOGGER.info("Received request to create product with SKU={}", request.sku());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        productService.createProduct(request),
                        "Product created",
                        201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts() {
        LOGGER.info("Received request to get all products");

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        productService.getProducts(),
                        "Products retrieved",
                        200)
                );
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID productId) {
        LOGGER.info("Received request to get product with id={}", productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        productService.getProductById(productId),
                        "Product retrieved",
                        200)
                );
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(@PathVariable String sku) {
        LOGGER.info("Received request to get product with SKU={}", sku);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        productService.getProductBySku(sku),
                        "Product retrieved",
                        200)
                );
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        LOGGER.info("Received request to update product with id={}", productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        productService.updateProduct(productId, request),
                        "Product updated",
                        200)
                );
    }

    @DeleteMapping("/id/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @PathVariable UUID productId) {
        productService.deleteProduct(productId);
        LOGGER.info("Received request to delete product with id={}", productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Product deleted")
                );
    }
}