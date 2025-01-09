package monaditto.cinemaproject.screening;

import java.time.LocalDateTime;

public record ScreeningDto(
        Long id,
        Long movieId,
        Long movieRoomId,
        LocalDateTime start,
        Double price
) {
        public static ScreeningDto screeningToScreeningDto(Screening screening) {
                if (screening == null) {
                        return null;
                }

                return new ScreeningDto(
                        screening.getScreeningId(),
                        screening.getMovie().getId(),
                        screening.getRoom().getMovieRoomId(),
                        screening.getStart(),
                        screening.getPrice()
                );
        }
}
