package monaditto.cinemaproject.role;

public record RoleDto(
        Long id,
        String name
) {
    public static RoleDto roleToRoleDto(Role role) {
        if (role == null) {
            return null;
        }

        return new RoleDto(
                role.getId(),
                role.getName()
        );
    }
}
