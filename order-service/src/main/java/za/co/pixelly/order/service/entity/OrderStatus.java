package za.co.pixelly.order.service.entity;

public enum OrderStatus {
    PENDING,        // order created, waiting for downstream processing
    CONFIRMED,      // order completed successfully
    FAILED,         // order creation failed or compensation required
    CANCELLED       // order cancelled by user/admin
}
