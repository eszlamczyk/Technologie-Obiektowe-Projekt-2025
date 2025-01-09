package monaditto.cinemaproject.screening;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    public List<Screening> findByStartBetween(LocalDateTime dateFrom, LocalDateTime dateTo);

    public List<Screening> findByStartAfter(LocalDateTime date);

    @Query("""
        SELECT s FROM Screening s
        WHERE s.room.id = :roomId
        AND s.start < :end
        AND s.start + s.movie.duration MINUTE > :start
    """)
    List<Screening> findConflictingScreenings(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
