package monaditto.cinemafront.controller.DTO;

import monaditto.cinemafront.databaseMapping.RoleDto;
import java.util.List;

public record AuthResponse(Long userID, String token, List<RoleDto> roles) {}
