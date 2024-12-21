package monaditto.cinemafront.databaseMapping;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String password
) {
}
