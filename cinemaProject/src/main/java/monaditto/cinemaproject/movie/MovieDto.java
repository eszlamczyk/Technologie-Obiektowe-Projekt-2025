package monaditto.cinemaproject.movie;

import java.time.LocalDate;

public record MovieDto(
        Long id,
        String title,
        String description,
        int duration,
        String posterUrl,
        LocalDate releaseDate
) {
    public MovieDto(String title, String description, int duration, String posterUrl, LocalDate releaseDate) {
        this(null, title, description, duration, posterUrl, releaseDate);
    }

    public static MovieDto movieToMovieDto(Movie movie) {
        if (movie == null) {
            return null;
        }

        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDuration(),
                movie.getPosterUrl(),
                movie.getReleaseDate()
        );
    }
}
