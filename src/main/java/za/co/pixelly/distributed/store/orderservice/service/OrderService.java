package za.co.pixelly.distributed.store.orderservice.service;

import za.co.pixelly.distributed.store.orderservice.dto.OrderRequest;
import za.co.pixelly.distributed.store.orderservice.dto.OrderResponse;
import za.co.pixelly.distributed.store.orderservice.dto.OrderStatusRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> getOrders();

    OrderResponse getOrder(String orderId);

    OrderResponse updateOrderStatus(String orderId, OrderStatusRequest request);

    void deleteOrder(String orderId);
}
