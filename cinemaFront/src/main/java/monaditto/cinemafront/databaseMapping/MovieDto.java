package monaditto.cinemafront.databaseMapping;

import java.time.LocalDate;

public record MovieDto(
        Long id,
        String title,
        String description,
        int duration,
        String posterUrl,
        LocalDate releaseDate
) {}
