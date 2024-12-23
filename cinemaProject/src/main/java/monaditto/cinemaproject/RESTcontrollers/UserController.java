package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.status.Status;
import monaditto.cinemaproject.user.CreateUserStatus;
import monaditto.cinemaproject.user.UserDto;
import monaditto.cinemaproject.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto userDto
    ) {

        Status editStatus = userService.editUser(userId, userDto);

        if (editStatus.isSuccess()) {
            return ResponseEntity.ok(editStatus.message());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(editStatus.message());
    }

}
