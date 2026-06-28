package za.co.pixelly.order.service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.pixelly.order.service.client.ProductClient;
import za.co.pixelly.order.service.client.dto.ProductResponse;
import za.co.pixelly.order.service.dto.OrderRequest;
import za.co.pixelly.order.service.dto.OrderResponse;
import za.co.pixelly.order.service.dto.OrderStatusRequest;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.exception.OrderNotFoundException;
import za.co.pixelly.order.service.repository.OrderRepository;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOrderService.class);
    private final ProductClient productClient;
    private final OrderCreationTransactionService orderCreationTransactionService;

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        UUID reservationId = UUID.randomUUID();

        ProductResponse reservedProduct = productClient.reserveStock(
                request.productId(),
                reservationId,
                request.quantity()
        );

        try {
            return orderCreationTransactionService.saveOrderAndOutbox(
                    request,
                    reservedProduct
            );

        } catch (Exception ex) {
            compensateStockReservation(request, reservationId, ex);
            throw ex;
        }
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

    private void compensateStockReservation(
            OrderRequest request,
            UUID reservationId,
            Exception originalException
    ) {
        try {
            LOGGER.warn(
                    "Order creation failed after stock reservation. Releasing stock. productId={}, reservationId={}, quantity={}, error={}",
                    request.productId(),
                    reservationId,
                    request.quantity(),
                    originalException.getMessage()
            );

            productClient.releaseStock(
                    request.productId(),
                    reservationId,
                    request.quantity()
            );

            LOGGER.info(
                    "Stock compensation completed. productId={}, quantity={}",
                    request.productId(),
                    request.quantity()
            );
        } catch (Exception compensationException) {
            LOGGER.error(
                    "Stock compensation failed. Manual investigation required. productId={}, reservationId={}, quantity={}, originalError={}, compensationError={}",
                    request.productId(),
                    reservationId,
                    request.quantity(),
                    originalException.getMessage(),
                    compensationException.getMessage()
            );
        }
    }
}
