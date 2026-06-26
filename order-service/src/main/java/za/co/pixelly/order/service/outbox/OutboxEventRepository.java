package za.co.pixelly.order.service.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);

    List<OutboxEvent> findTop20ByStatusOrderByCreatedAtDesc(OutboxEventStatus status);

    Optional<OutboxEvent> findByEventId(UUID eventId);
}
