package monaditto.cinemafront.databaseMapping;

import java.time.LocalDateTime;

public record PurchaseResponseDto(
        Long id,
        Long userId,
        Long screeningId,
        String userName,
        String movieTitle,
        LocalDateTime screeningTime,
        int boughtSeats,
        ReservationStatus status
) {}

