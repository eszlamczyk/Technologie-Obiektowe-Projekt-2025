package monaditto.cinemafront.databaseMapping;

public record MovieRoomDto(
        Long id,
        String movieRoomName,
        int maxSeats
) {}
