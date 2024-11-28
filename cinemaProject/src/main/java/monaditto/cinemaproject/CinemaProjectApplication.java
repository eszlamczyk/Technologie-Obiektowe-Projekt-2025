package monaditto.cinemaproject;

import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CinemaProjectApplication {

    public static void main(String[] args) {
//        SpringApplication.run(CinemaProjectApplication.class, args);

        ConfigurableApplicationContext context = SpringApplication.run(CinemaProjectApplication.class, args);
        UserRepository userRepository = context.getBean(UserRepository.class);

        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");

        userRepository.save(user);
        System.out.println("User saved to database!");
    }

}
