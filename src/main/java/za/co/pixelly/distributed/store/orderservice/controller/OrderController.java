package za.co.pixelly.distributed.store.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.pixelly.distributed.store.orderservice.dto.ApiResponse;
import za.co.pixelly.distributed.store.orderservice.dto.OrderRequest;
import za.co.pixelly.distributed.store.orderservice.dto.OrderResponse;
import za.co.pixelly.distributed.store.orderservice.dto.OrderStatusRequest;
import za.co.pixelly.distributed.store.orderservice.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        orderService.createOrder(request),
                        "Order created",
                        HttpStatus.CREATED.value()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        orderService.getOrders(),
                        "Orders retrieved",
                        HttpStatus.OK.value()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable String orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        orderService.getOrder(orderId),
                        "Order retrieved",
                        HttpStatus.OK.value()
                ));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody OrderStatusRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        orderService.updateOrderStatus(orderId, request),
                        "Order status updated", HttpStatus.OK.value()
                ));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(
            @PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Order deleted"));
    }
}
