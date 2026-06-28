package za.co.pixelly.product.service.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.pixelly.product.service.entity.StockReservation;

import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

    Optional<StockReservation> findByReservationId(UUID reservationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select sr
            from StockReservation sr
            where sr.reservationId = :reservationId
            """)
    Optional<StockReservation> findByReservationIdWithLock(
            @Param("reservationId") UUID reservationId
    );
}
