package monaditto.cinemaproject.RESTcontrollers;

import jakarta.annotation.security.RolesAllowed;
import monaditto.cinemaproject.user.UserDto;
import monaditto.cinemaproject.user.UserService;
import monaditto.cinemaproject.user.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-panel")
public class AdminPanelController {

    private final UserService userService;

    public AdminPanelController(UserService userService) {
        this.userService = userService;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.getUsers();
    }

    @RolesAllowed("ADMIN")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}