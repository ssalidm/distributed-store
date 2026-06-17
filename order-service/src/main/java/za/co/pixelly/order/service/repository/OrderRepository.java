package za.co.pixelly.order.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.pixelly.order.service.entity.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
