package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.RESTcontrollers.DTO.AuthResponse;
import monaditto.cinemaproject.RESTcontrollers.DTO.LoginRequest;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if (isAuthenticated) {
            User user = userService.findByEmail(loginRequest.getEmail());
            return ResponseEntity.ok(new AuthResponse(user.getRoles().stream().toList()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}