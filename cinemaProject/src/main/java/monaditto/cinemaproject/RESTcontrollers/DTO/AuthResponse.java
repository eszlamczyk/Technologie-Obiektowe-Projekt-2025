package monaditto.cinemaproject.RESTcontrollers.DTO;

import monaditto.cinemaproject.role.RoleDto;

import java.util.List;

public class AuthResponse {
    private Long userId;

    private List<RoleDto> roles;

    public AuthResponse() {}

    public AuthResponse(Long userId, List<RoleDto> roles) {
        this.userId = userId;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}