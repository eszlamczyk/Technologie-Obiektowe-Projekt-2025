package monaditto.cinemaproject.user;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String password
) {
    public static UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword()
        );
    }
}
