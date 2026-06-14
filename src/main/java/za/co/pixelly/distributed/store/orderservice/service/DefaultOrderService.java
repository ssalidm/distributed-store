package za.co.pixelly.distributed.store.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.pixelly.distributed.store.orderservice.dto.OrderRequest;
import za.co.pixelly.distributed.store.orderservice.dto.OrderResponse;
import za.co.pixelly.distributed.store.orderservice.dto.OrderStatusRequest;
import za.co.pixelly.distributed.store.orderservice.entity.Order;
import za.co.pixelly.distributed.store.orderservice.exception.OrderNotFoundException;
import za.co.pixelly.distributed.store.orderservice.repository.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order savedOrder = orderRepository.saveAndFlush(toEntity(request));
        return OrderResponse.from(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrders() {
//        List<OrderResponse> orders = new ArrayList<>();
//        for (Order o : orderRepository.findAll()) {
//            orders.add(OrderResponse.from(o));
//        }
//        return orders;
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    @Override
    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found"));
        return OrderResponse.from(order);
    }

    @Override
    public OrderResponse updateOrderStatus(String orderId, OrderStatusRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found"));
        order.setStatus(request.status());

        return OrderResponse.from(orderRepository.saveAndFlush(order));
    }

    @Override
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    private static Order toEntity(OrderRequest request) {
        return Order.builder()
                .customerName(request.customerName())
                .productName(request.productName())
                .quantity(request.quantity())
                .build();
    }
}
