package za.co.pixelly.order.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.pixelly.order.service.client.ProductClient;
import za.co.pixelly.order.service.client.dto.ProductResponse;
import za.co.pixelly.order.service.dto.OrderRequest;
import za.co.pixelly.order.service.dto.OrderResponse;
import za.co.pixelly.order.service.dto.OrderStatusRequest;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.exception.InsufficientStockException;
import za.co.pixelly.order.service.exception.OrderNotFoundException;
import za.co.pixelly.order.service.messaging.OrderEventPublisher;
import za.co.pixelly.order.service.outbox.OutboxEventService;
import za.co.pixelly.order.service.repository.OrderRepository;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher orderEventPublisher;
    private final OutboxEventService outboxEventService;

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        ProductResponse product = productClient.getProduct(request.productId());

        if (product.stockQuantity() < request.quantity()) {
            throw new InsufficientStockException("Not enough stock available");
        }

        Order newOrder = buildOrder(request, product);
        Order savedOrder = orderRepository.saveAndFlush(newOrder);

        outboxEventService.saveOrderCreatedEvent(savedOrder);

//        orderEventPublisher.publishOrderCreated(savedOrder);

        return OrderResponse.from(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Override
    public OrderResponse getOrder(UUID orderId) {
        return OrderResponse.from(findOrder(orderId));
    }

    @Transactional
    @Override
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatusRequest request) {
        Order order = findOrder(orderId);
        order.setStatus(request.status());

        return OrderResponse.from(orderRepository.saveAndFlush(order));
    }

    @Transactional
    @Override
    public void deleteOrder(UUID orderId) {
        Order order = findOrder(orderId);
        orderRepository.delete(order);
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
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
