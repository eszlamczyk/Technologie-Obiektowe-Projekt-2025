package monaditto.cinemaproject.RESTcontrollers;


import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService){
        this.userService = userService;
    }


    @PostMapping
    public String register(@RequestBody UserService.UserDto userDto){
        return userService.createUser(userDto).toString();
    }

}
