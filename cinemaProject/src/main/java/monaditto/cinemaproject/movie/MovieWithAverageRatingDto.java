package monaditto.cinemaproject.movie;

public record MovieWithAverageRatingDto(

        MovieDto movieDto,

        Double averageRating
) {}
