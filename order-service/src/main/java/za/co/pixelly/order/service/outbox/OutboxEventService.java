package za.co.pixelly.order.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.messaging.event.OrderCreatedEvent;


@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private static final String ORDER_CREATED_EVENT = "OrderCreated";
    private static final String ORDER_AGGREGATE = "Order";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${messaging.order.exchange}")
    private String orderExchange;

    @Value("${messaging.order.routing-key}")
    private String orderCreatedRoutingKey;

    public void saveOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = OrderCreatedEvent.from(order);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventId(event.eventId())
                .eventType(ORDER_CREATED_EVENT)
                .aggregateType(ORDER_AGGREGATE)
                .aggregateId(order.getId())
                .exchangeName(orderExchange)
                .routingKey(orderCreatedRoutingKey)
                .payload(toJson(event))
                .status(OutboxEventStatus.PENDING)
                .retryCount(0)
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    private String toJson(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize OrderCreated event", ex);
        }
    }
}
