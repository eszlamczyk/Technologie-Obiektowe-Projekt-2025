package monaditto.cinemaproject.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
    List<Purchase> findByScreeningId(Long screeningId);
    List<Purchase> findByReservationStatus(ReservationStatus status);

    @Query("SELECT SUM(p.boughtSeats * s.price) FROM Purchase p " +
            "JOIN p.screening s WHERE p.reservationStatus = 'PAID' " +
            "AND s.start BETWEEN :startDate AND :endDate")
    Double calculateRevenueBetween(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.boughtSeats * s.price) FROM Purchase p " +
            "JOIN p.screening s WHERE p.reservationStatus = 'PAID' " +
            "AND s.start >= :startDate")
    Double calculateRevenueSince(@Param("startDate") LocalDateTime startDate);

    List<Purchase> findAllByScreeningStartAfter(LocalDateTime localDateTime);

    @Query("SELECT COALESCE(SUM(p.boughtSeats), 0) FROM Purchase p " +
            "JOIN p.screening s WHERE p.reservationStatus = 'PAID' " +
            "AND s.start BETWEEN :startDate AND :endDate")
    long calculateTotalSeatsForPeriod(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);


    @Query("SELECT COUNT(DISTINCT s) FROM Purchase p " +
            "JOIN p.screening s WHERE p.reservationStatus = 'PAID' " +
            "AND s.start BETWEEN :startDate AND :endDate")
    long calculateTotalScreeningsForPeriod(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}
