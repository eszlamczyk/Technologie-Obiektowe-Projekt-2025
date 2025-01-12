package monaditto.cinemafront.databaseMapping;

public record MovieWithAverageRatingDto(

        MovieDto movieDto,

        Double averageRating
) {}
