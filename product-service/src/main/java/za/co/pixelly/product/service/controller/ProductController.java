package za.co.pixelly.product.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.pixelly.product.service.dto.ApiResponse;
import za.co.pixelly.product.service.dto.ProductRequest;
import za.co.pixelly.product.service.dto.ProductResponse;
import za.co.pixelly.product.service.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        productService.createProduct(request),
                        "Product created",
                        HttpStatus.CREATED.value()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        productService.getProducts(),
                        "Products retrieved",
                        HttpStatus.OK.value()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        productService.getProduct(productId),
                        "Product retrieved",
                        HttpStatus.OK.value()));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Product deleted"));
    }
}