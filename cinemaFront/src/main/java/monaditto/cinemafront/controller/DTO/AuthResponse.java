package monaditto.cinemafront.controller.DTO;

import monaditto.cinemafront.databaseMapping.Role;

import java.util.List;

public class AuthResponse {
    private List<Role> roles;

    public AuthResponse() {}

    public AuthResponse(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}