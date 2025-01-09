package monaditto.cinemaproject.RESTcontrollers;

import jakarta.servlet.http.HttpSession;
import monaditto.cinemaproject.RESTcontrollers.DTO.AuthResponse;
import monaditto.cinemaproject.RESTcontrollers.DTO.LoginRequest;
import monaditto.cinemaproject.jwt.JwtUtil;
import monaditto.cinemaproject.role.RoleDto;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserDto;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final RoleService roleService;

    @Autowired
    public AuthController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        System.out.println("Person of credentials: " + loginRequest.getEmail() +
                " " + loginRequest.getPassword() + " is trying to log in");
        boolean isAuthenticated = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        Optional<UserDto> optionalUserDto = userService.findByEmail(loginRequest.getEmail());

        if (isAuthenticated) {
            UserDto userDto = optionalUserDto.get();
            List<String> roles = roleService.getUserRoles(userDto.id())
                    .stream()
                    .map(RoleDto::name)
                    .toList();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDto.email(), null, roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            return ResponseEntity.ok(
                    new AuthResponse(userDto.id(), roleService.getUserRoles(userDto.id()))
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}