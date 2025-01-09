package monaditto.cinemafront.databaseMapping;

public record MovieRoomDto(
        Long id,
        String movieRoomName,
        int maxSeats
) {
    public MovieRoomDto(String movieRoomName, int maxSeats){this(null,movieRoomName,maxSeats);}
}
