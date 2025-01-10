package monaditto.cinemaproject.user;

import monaditto.cinemaproject.role.RoleDto;
import monaditto.cinemaproject.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public CustomUserDetailsService(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = userService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email " + username + " not found!"
                ));

        List<RoleDto> roles = roleService.getUserRoles(user.id());
        return new CustomUserDetails(user.email(), user.password(), roles);
    }
}
