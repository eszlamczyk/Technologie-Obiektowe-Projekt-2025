package monaditto.cinemafront.databaseMapping;

public record MovieDto(
        Long id,
        String title,
        String description,
        int duration,
        String posterUrl
) {}
