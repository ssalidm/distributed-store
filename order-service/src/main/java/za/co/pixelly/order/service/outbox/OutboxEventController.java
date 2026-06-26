package za.co.pixelly.order.service.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/outbox-events")
@RequiredArgsConstructor
public class OutboxEventController {

    private final OutboxEventReplayService outboxEventReplayService;

    @GetMapping("/failed")
    public ResponseEntity<List<OutboxEventResponse>> getFailedEvents() {
        return ResponseEntity.ok(outboxEventReplayService.getFailedEvents());
    }

    @PostMapping("/{eventId}/replay")
    public ResponseEntity<OutboxEventResponse> replayFailedEvent(@PathVariable UUID eventId) {
        return ResponseEntity.ok(outboxEventReplayService.replayFailedEvent(eventId));
    }
}
