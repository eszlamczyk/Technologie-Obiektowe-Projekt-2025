package monaditto.cinemaproject.movie;

import jakarta.persistence.Column;

public record MovieDto(
        Long id,

        String title,

        String description,

        int duration,

        String posterUrl
) {}
