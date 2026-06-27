package za.co.pixelly.order.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.pixelly.order.service.client.dto.ProductResponse;
import za.co.pixelly.order.service.dto.OrderRequest;
import za.co.pixelly.order.service.dto.OrderResponse;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.outbox.OutboxEventService;
import za.co.pixelly.order.service.repository.OrderRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderCreationTransactionService {

    private final OrderRepository orderRepository;
    private final OutboxEventService outboxEventService;

    @Transactional
    public OrderResponse saveOrderAndOutbox(OrderRequest request, ProductResponse product) {
        Order order = buildOrder(request, product);
        Order savedOrder = orderRepository.saveAndFlush(order);

        outboxEventService.saveOrderCreatedEvent(savedOrder);
        return OrderResponse.from(savedOrder);
    }

    private Order buildOrder(OrderRequest request, ProductResponse product) {
        return Order.builder()
                .customerName(request.customerName())
                .productId(request.productId())
                .productName(product.name())
                .sku(product.sku())
                .unitPrice(product.price())
                .quantity(request.quantity())
                .totalAmount(product.price().multiply(BigDecimal.valueOf(request.quantity())))
                .build();
    }
}
