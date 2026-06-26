package za.co.pixelly.order.service.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventReplayService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional(readOnly = true)
    public List<OutboxEventResponse> getFailedEvents() {
        return outboxEventRepository
                .findTop20ByStatusOrderByCreatedAtDesc(OutboxEventStatus.FAILED)
                .stream()
                .map(OutboxEventResponse::from)
                .toList();
    }

    @Transactional
    public OutboxEventResponse replayFailedEvent(UUID eventId) {
        OutboxEvent event = outboxEventRepository.findByEventId(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Outbox event not found with eventId=" + eventId
                ));

        if (event.getStatus() != OutboxEventStatus.FAILED) {
            throw new IllegalStateException("Only FAILED outbox events can be replayed. Current status=" + event.getStatus()
            );
        }

        event.setStatus(OutboxEventStatus.PENDING);
        event.setRetryCount(0);
        event.setLastError(null);
        event.setPublishedAt(null);

        OutboxEvent savedEvent = outboxEventRepository.save(event);

        return OutboxEventResponse.from(savedEvent);
    }

}
