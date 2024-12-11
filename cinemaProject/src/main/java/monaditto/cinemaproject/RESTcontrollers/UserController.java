package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long userId,
            @RequestBody User user
    ) {
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User existingUser = optionalUser.get();
        UserService.UserDto oldUser = new UserService.UserDto(existingUser.getEmail(), existingUser.getFirstName(),
                existingUser.getLastName(), existingUser.getPassword());
        UserService.UserDto userDto = new UserService.UserDto(user.getEmail(),user.getFirstName(),user.getLastName(),user.getPassword());
        userService.editUser(oldUser, userDto);
        optionalUser = null;

        optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        existingUser = optionalUser.get();

        System.out.println(existingUser.getFirstName() +" "+ existingUser.getEmail() +" "+ existingUser.getLastName());

        return ResponseEntity.ok().build();
    }

}
