package za.co.pixelly.order.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.pixelly.order.service.client.ProductClient;
import za.co.pixelly.order.service.client.dto.ProductResponse;
import za.co.pixelly.order.service.dto.OrderRequest;
import za.co.pixelly.order.service.dto.OrderResponse;
import za.co.pixelly.order.service.dto.OrderStatusRequest;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.exception.InsufficientStockException;
import za.co.pixelly.order.service.exception.OrderNotFoundException;
import za.co.pixelly.order.service.repository.OrderRepository;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        ProductResponse product = productClient.getProduct(request.productId());

        if (product.stockQuantity() < request.quantity()) {
            throw new InsufficientStockException("Not enough stock available");
        }

        Order newOrder = Order.builder()
                .customerName(request.customerName())
                .productId(request.productId())
                .productName(product.name())
                .sku(product.sku())
                .unitPrice(product.price())
                .quantity(request.quantity())
                .totalAmount(product.price().multiply(BigDecimal.valueOf(request.quantity())))
                .build();

        return OrderResponse.from(orderRepository.saveAndFlush(newOrder));
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

    @Override
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatusRequest request) {
        Order order = findOrder(orderId);
        order.setStatus(request.status());

        return OrderResponse.from(orderRepository.saveAndFlush(order));
    }

    @Override
    public void deleteOrder(UUID orderId) {
        Order order = findOrder(orderId);
        orderRepository.delete(order);
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
}
