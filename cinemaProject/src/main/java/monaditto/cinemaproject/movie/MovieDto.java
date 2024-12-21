package monaditto.cinemaproject.movie;

import jakarta.persistence.Column;
import monaditto.cinemaproject.screening.Screening;
import monaditto.cinemaproject.screening.ScreeningDto;

public record MovieDto(
        Long id,

        String title,

        String description,

        int duration,

        String posterUrl
) {
    public static MovieDto movieToMovieDto(Movie movie) {
        if (movie == null) {
            return null;
        }

        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDuration(),
                movie.getPosterUrl()
        );
    }
}
