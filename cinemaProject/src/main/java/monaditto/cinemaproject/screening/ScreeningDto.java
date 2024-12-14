package monaditto.cinemaproject.screening;

import java.time.LocalDateTime;

public record ScreeningDto(
        Long movieId,
        Long movieRoomId,
        LocalDateTime start,
        Double price
        ) {
}
