package za.co.pixelly.product.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.pixelly.product.service.dto.ProductCreateRequest;
import za.co.pixelly.product.service.dto.ProductResponse;
import za.co.pixelly.product.service.dto.ProductUpdateRequest;
import za.co.pixelly.product.service.entity.Product;
import za.co.pixelly.product.service.entity.StockReservation;
import za.co.pixelly.product.service.entity.StockReservationStatus;
import za.co.pixelly.product.service.exception.InsufficientStockException;
import za.co.pixelly.product.service.exception.ProductNotFoundException;
import za.co.pixelly.product.service.exception.StockReservationException;
import za.co.pixelly.product.service.repository.ProductRepository;
import za.co.pixelly.product.service.repository.StockReservationRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;
    private final StockReservationRepository stockReservationRepository;

    @Transactional
    @Override
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
        return ProductResponse.from(findProduct(productId));
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findProductBySku(sku).orElseThrow(
                ProductNotFoundException::new
        );
        return ProductResponse.from(product);
    }

    @Transactional
    @Override
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

    @Transactional
    @Override
    public void deleteProduct(UUID productId) {
        productRepository.delete(findProduct(productId));
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId).orElseThrow(
                ProductNotFoundException::new);
    }

    @Transactional
    @Override
    public ProductResponse reserveStock(UUID productId, UUID reservationId, Integer quantity) {
        return stockReservationRepository.findByReservationId(reservationId)
                .map(existingReservation -> handleExistingReservation(
                        productId, reservationId, quantity, existingReservation))
                .orElseGet(() -> createNewReservation(productId, reservationId, quantity));
    }

    private ProductResponse handleExistingReservation(
            UUID productId,
            UUID reservationId,
            Integer quantity,
            StockReservation existingReservation
    ) {
        if (!existingReservation.getProductId().equals(productId)) {
            throw new StockReservationException("Reservation ID already belongs to another product");
        }

        if (!existingReservation.getQuantity().equals(quantity)) {
            throw new StockReservationException("Reservation ID already exists with a different quantity");
        }

        if (existingReservation.getStatus() == StockReservationStatus.RELEASED) {
            throw new StockReservationException("Reservation was already released");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        return ProductResponse.from(product);
    }

    private ProductResponse createNewReservation(UUID productId, UUID reservationId, Integer quantity) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(ProductNotFoundException::new);

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available stock: " + product.getStockQuantity());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);

        StockReservation reservation = StockReservation.builder()
                .reservationId(reservationId)
                .productId(productId)
                .quantity(quantity)
                .status(StockReservationStatus.RESERVED)
                .build();

        stockReservationRepository.save(reservation);

        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponse releaseStock(UUID productId, UUID reservationId) {
        StockReservation reservation = stockReservationRepository.findByReservationIdWithLock(reservationId)
                .orElseThrow(() -> new StockReservationException("Stock reservation not found"));

        if (!reservation.getProductId().equals(productId)) {
            throw new StockReservationException("Reservation does not belong to this product");
        }

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(ProductNotFoundException::new);

        if (reservation.getStatus() == StockReservationStatus.RELEASED) {
            return ProductResponse.from(product);
        }

        product.setStockQuantity(product.getStockQuantity() + reservation.getQuantity());
        reservation.setStatus(StockReservationStatus.RELEASED);
        stockReservationRepository.save(reservation);

        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
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
