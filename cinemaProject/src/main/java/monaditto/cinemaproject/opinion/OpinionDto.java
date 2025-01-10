package monaditto.cinemaproject.opinion;

public record OpinionDto(
        Long userId,
        Long movieId,
        Double rating,
        String comment
) {
    public static OpinionDto opinionToOpinionDto(Opinion opinion) {
        if (opinion == null) {
            return null;
        }

        return new OpinionDto(
                opinion.getUser().getId(),
                opinion.getMovie().getId(),
                opinion.getRating(),
                opinion.getComment()
        );
    }
}
