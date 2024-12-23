package monaditto.cinemafront.controller.DTO;

import monaditto.cinemafront.databaseMapping.RoleDto;

import java.util.List;

public class AuthResponse {
    private List<RoleDto> roles;

    public AuthResponse() {}

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