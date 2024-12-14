package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RolesController {
    UserService userService;
    RoleService roleService;

    @Autowired
    public RolesController(UserService userService, RoleService roleService){
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/assigned/{userId}")
    List<Role> getUserRoles(@PathVariable Long userId){
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()){
            return List.of();
        }
        User user = optionalUser.get();
        return roleService.getUserRoles(user);
    }

    @GetMapping("/available/{userId}")
    List<Role> getAvailableRolesForUser(@PathVariable Long userId){
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()){
            return List.of();
        }
        User user = optionalUser.get();
        return roleService.getAvailableRoles(user);
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<Void> updateRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds
    ) {
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<Role> newRoles = roleService.findRolesByIds(roleIds);

        User user = optionalUser.get();
        roleService.updateRoles(user,newRoles);
        return ResponseEntity.ok().build();
    }
}

