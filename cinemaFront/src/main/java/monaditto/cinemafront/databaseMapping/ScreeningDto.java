package monaditto.cinemafront.databaseMapping;

import java.time.LocalDateTime;

public record ScreeningDto(
        Long id,

        Long movieId,

        Long movieRoomId,

        LocalDateTime start,

        Double price
) {}
