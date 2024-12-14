package monaditto.cinemaproject.user;

public record UserDto(
        String email,
        String firstName,
        String lastName,
        String password
) {
}
