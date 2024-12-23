package monaditto.cinemafront.databaseMapping;

public record OpinionDto(
        Long userId,

        Long movieId,

        Double rating,

        String comment
) {}
