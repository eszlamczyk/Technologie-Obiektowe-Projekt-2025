package monaditto.cinemaproject.RESTcontrollers;

import jakarta.servlet.http.HttpSession;
import monaditto.cinemaproject.RESTcontrollers.DTO.AuthResponse;
import monaditto.cinemaproject.RESTcontrollers.DTO.LoginRequest;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.user.CustomUserDetails;
import monaditto.cinemaproject.user.UserDto;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final RoleService roleService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, RoleService roleService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String test(){
        return "Test";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            // Let Spring Security handle the authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );


            // Get the authenticated user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserDto userDto = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));


            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/; HttpOnly");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new AuthResponse(userDto.id(), roleService.getUserRoles(userDto.id())));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}