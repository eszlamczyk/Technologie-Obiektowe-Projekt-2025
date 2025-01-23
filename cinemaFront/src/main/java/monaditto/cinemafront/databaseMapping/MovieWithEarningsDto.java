package monaditto.cinemafront.databaseMapping;

public record MovieWithEarningsDto(
        Long movieId,
        String title,
        Double earnings
) { }
