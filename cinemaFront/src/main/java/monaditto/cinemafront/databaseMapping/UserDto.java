package monaditto.cinemafront.databaseMapping;

public record UserDto(
        String email,
        String firstName,
        String lastName,
        String password
) {
}
