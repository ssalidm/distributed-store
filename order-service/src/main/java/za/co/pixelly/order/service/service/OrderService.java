package za.co.pixelly.order.service.service;

import za.co.pixelly.order.service.dto.OrderRequest;
import za.co.pixelly.order.service.dto.OrderResponse;
import za.co.pixelly.order.service.dto.OrderStatusRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> getOrders();

    OrderResponse getOrder(String orderId);

    OrderResponse updateOrderStatus(String orderId, OrderStatusRequest request);

    void deleteOrder(String orderId);
}
