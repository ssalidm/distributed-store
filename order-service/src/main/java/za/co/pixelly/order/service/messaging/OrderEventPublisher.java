package za.co.pixelly.order.service.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import za.co.pixelly.order.service.entity.Order;
import za.co.pixelly.order.service.messaging.event.OrderCreatedEvent;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${messaging.order.exchange}")
    private String orderExchange;

    @Value("${messaging.order.routing-key}")
    private String orderCreatedRoutingKey;

    public void publishOrderCreated(Order order) {
        try {
            LOGGER.info("➡️➡️➡️ [ORDER-SERVICE] Dispatching order '{}' to the message broker...", order.getId());

            OrderCreatedEvent event = OrderCreatedEvent.from(order);

            rabbitTemplate.convertAndSend(orderExchange, orderCreatedRoutingKey, event);

            LOGGER.info("✅ [ORDER-SERVICE] Order '{}' successfully queued!", order.getId());
        } catch (Exception e) {
            LOGGER.error("❗Failed to publish order to RabbitMQ: ", e);
        }
    }
}
