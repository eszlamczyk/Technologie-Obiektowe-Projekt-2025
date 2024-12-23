package monaditto.cinemaproject.RESTcontrollers.DTO;

import monaditto.cinemaproject.role.RoleDto;

import java.util.List;

public class AuthResponse {
    private List<RoleDto> roles;

    public AuthResponse(List<RoleDto> roles) {
        this.roles = roles;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}