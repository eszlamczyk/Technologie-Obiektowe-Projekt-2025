package monaditto.cinemaproject;

import monaditto.cinemaproject.crypto.PasswordHasher;
import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfiguration {

    @Bean
    CommandLineRunner initData(UserService userService, RoleService roleService, PasswordHasher passwordHasher){
        return args -> {
            if (userService.getUsers().isEmpty()) {
                Role admin = roleService.createRole("admin");
                Role user = roleService.createRole("user");




                User administrator = new User("admin", "admin", "admin@admin.admin",
                        passwordHasher.hashPassword("admin"));

                List<User> users = new ArrayList<>();

                users.add(new User("Janusz", "Nowak", "janusz.nowak@gmail.com",
                        passwordHasher.hashPassword("KochamŻone.")));
                users.add(new User("Anna", "Kowalska", "anna.kowalska@gmail.com",
                        passwordHasher.hashPassword("Mocna@123")));
                users.add(new User("Piotr", "Wiśniewski", "piotr.wisniewski@gmail.com",
                        passwordHasher.hashPassword("Hejka$456")));
                users.add(new User("Ewa", "Zielińska", "ewa.zielinska@gmail.com",
                        passwordHasher.hashPassword("EwaZ1234!")));
                users.add(new User("Tomasz", "Kamiński", "tomasz.kaminski@gmail.com",
                        passwordHasher.hashPassword("Tommy#789")));
                users.add(new User("Magda", "Lewandowska", "magda.lewandowska@gmail.com",
                        passwordHasher.hashPassword("Magda&654")));
                users.add(new User("Robert", "Nowicki", "robert.nowicki@gmail.com",
                        passwordHasher.hashPassword("Nowicki!321")));
                users.add(new User("Karolina", "Mazur", "karolina.mazur@gmail.com",
                        passwordHasher.hashPassword("Mazur@987")));
                users.add(new User("Paweł", "Wojciechowski", "pawel.wojciechowski@gmail.com",
                        passwordHasher.hashPassword("Paweł^4567")));
                users.add(new User("Marta", "Pawlak", "marta.pawlak@gmail.com",
                        passwordHasher.hashPassword("Marta+891")));

                users.add(new User("Janusz", "Nowak", "1janusz.nowak@gmail.com",
                        passwordHasher.hashPassword("KochamŻone.")));
                users.add(new User("Anna", "Kowalska", "1anna.kowalska@gmail.com",
                        passwordHasher.hashPassword("Mocna@123")));
                users.add(new User("Piotr", "Wiśniewski", "1piotr.wisniewski@gmail.com",
                        passwordHasher.hashPassword("Hejka$456")));
                users.add(new User("Ewa", "Zielińska", "1ewa.zielinska@gmail.com",
                        passwordHasher.hashPassword("EwaZ1234!")));
                users.add(new User("Tomasz", "Kamiński", "1tomasz.kaminski@gmail.com",
                        passwordHasher.hashPassword("Tommy#789")));
                users.add(new User("Magda", "Lewandowska", "1magda.lewandowska@gmail.com",
                        passwordHasher.hashPassword("Magda&654")));
                users.add(new User("Robert", "Nowicki", "1robert.nowicki@gmail.com",
                        passwordHasher.hashPassword("Nowicki!321")));
                users.add(new User("Karolina", "Mazur", "1karolina.mazur@gmail.com",
                        passwordHasher.hashPassword("Mazur@987")));
                users.add(new User("Paweł", "Wojciechowski", "1pawel.wojciechowski@gmail.com",
                        passwordHasher.hashPassword("Paweł^4567")));
                users.add(new User("Marta", "Pawlak", "1marta.pawlak@gmail.com",
                        passwordHasher.hashPassword("Marta+891")));

                userService.save(administrator);

                roleService.addRoleToUser(administrator, user);
                roleService.addRoleToUser(administrator, admin);

                for (User basic_user : users) {
                    userService.save(basic_user);
                    roleService.addRoleToUser(basic_user, user);
                }
            }
        };
    }
}
