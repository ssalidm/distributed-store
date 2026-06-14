package za.co.pixelly.distributed.store.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.pixelly.distributed.store.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
}
