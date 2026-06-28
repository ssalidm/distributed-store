package za.co.pixelly.product.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_reservations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stock_reservations_reservation_id", columnNames = "reservation_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservation {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(name = "reservation_id", nullable = false, unique = true)
        private UUID reservationId;

        @Column(name = "product_id", nullable = false)
        private UUID productId;

        @Column(nullable = false)
        Integer quantity;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private StockReservationStatus status;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private Instant updatedAt;
}
