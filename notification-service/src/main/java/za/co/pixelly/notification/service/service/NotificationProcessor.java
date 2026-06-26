package za.co.pixelly.notification.service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.pixelly.notification.service.entity.ProcessedMessage;
import za.co.pixelly.notification.service.messaging.event.OrderCreatedEvent;
import za.co.pixelly.notification.service.repository.ProcessedMessageRepository;

@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationProcessor.class);

    private static final String EVENT_TYPE = "OrderCreated";
    private static final String SOURCE = "order-service";

    private final ProcessedMessageRepository processedMessageRepository;

    @Transactional
    public void processOrderCreated(OrderCreatedEvent event) {
        if (processedMessageRepository.existsByEventId(event.eventId())) {
            LOGGER.info(":::: Skipping duplicate OrderCreated event. eventId={}, orderId={} ::::",
                    event.eventId(),
                    event.orderId());
            return;
        }

        /*
         * This is where a real system would send email/SMS/WhatsApp.
         * For now, we log the notification.
         */
        LOGGER.info(
                ":::: Notification sent for orderId={}, customerName={}, productName={}, unitPrice={}, quantity={}, totalAmount={} ::::",
                event.orderId(),
                event.customerName(),
                event.productName(),
                event.unitPrice(),
                event.quantity(),
                event.totalAmount()
        );

        try {
            processedMessageRepository.save(
                    ProcessedMessage.builder()
                            .eventId(event.eventId())
                            .eventType(EVENT_TYPE)
                            .source(SOURCE)
                            .build()
            );
        } catch (DataIntegrityViolationException ex) {
            /*
             * This protects against a race condition where two consumers process
             * the same eventId at almost the same time.
             */
            LOGGER.info(":::: Duplicate event detected during save. eventId={}, orderId={} ::::",
                    event.eventId(),
                    event.orderId());
        }
    }
}
