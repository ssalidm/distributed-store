package za.co.pixelly.product.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.pixelly.product.service.entity.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findProductBySku(String sku);

    Boolean existsBySku(String sku);
}
