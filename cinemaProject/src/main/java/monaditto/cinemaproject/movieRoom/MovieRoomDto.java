package monaditto.cinemaproject.movieRoom;

public record MovieRoomDto(
        Long id,
        String movieRoomName,
        int maxSeats
) {

    public MovieRoomDto(String movieRoomName, int maxSeats) {
        this(null,movieRoomName, maxSeats);
    }

    public static MovieRoomDto movieRoomtoMovieRoomDto(MovieRoom movieRoom) {
        if (movieRoom == null) {
            return null;
        }

        return new MovieRoomDto(
                movieRoom.getMovieRoomId(),
                movieRoom.getMovieRoomName(),
                movieRoom.getMaxSeats()
        );
    }

}
