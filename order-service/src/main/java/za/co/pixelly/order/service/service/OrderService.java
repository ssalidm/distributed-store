package za.co.pixelly.order.service.service;

import za.co.pixelly.order.service.dto.OrderRequest;
import za.co.pixelly.order.service.dto.OrderResponse;
import za.co.pixelly.order.service.dto.OrderStatusRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> getOrders();

    OrderResponse getOrder(UUID orderId);

    OrderResponse updateOrderStatus(UUID orderId, OrderStatusRequest request);

    void deleteOrder(UUID orderId);
}
