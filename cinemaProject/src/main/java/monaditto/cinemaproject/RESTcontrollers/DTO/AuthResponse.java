package monaditto.cinemaproject.RESTcontrollers.DTO;

import monaditto.cinemaproject.role.RoleDto;

import java.util.List;

public record AuthResponse(Long userID, List<RoleDto> roles) {}
