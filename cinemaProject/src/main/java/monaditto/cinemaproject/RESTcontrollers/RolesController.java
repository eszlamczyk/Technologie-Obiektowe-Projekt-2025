package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleDto;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserDto;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
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
    List<RoleDto> getUserRoles(@PathVariable Long userId){
        Optional<UserDto> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()){
            return List.of();
        }
        UserDto userDto = optionalUser.get();
        return roleService.getUserRoles(userDto.id());
    }

    @GetMapping("/available/{userId}")
    List<RoleDto> getAvailableRolesForUser(@PathVariable Long userId){
        Optional<UserDto> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()){
            return List.of();
        }
        UserDto userDto = optionalUser.get();
        return roleService.getAvailableRoles(userDto.id());
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<Void> updateRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds
    ) {
        Optional<UserDto> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDto userDto = optionalUser.get();
        Set<Long> roleIdsSet = new HashSet<>(roleIds);
        roleService.updateRoles(userDto.id(), roleIdsSet);
        return ResponseEntity.ok().build();
    }
}

