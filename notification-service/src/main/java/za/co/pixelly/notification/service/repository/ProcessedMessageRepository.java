package za.co.pixelly.notification.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.pixelly.notification.service.entity.ProcessedMessage;

import java.util.UUID;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, UUID> {

    boolean existsByEventId(UUID eventId);
}
