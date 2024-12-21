package monaditto.cinemaproject;

import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.category.CategoryService;
import monaditto.cinemaproject.crypto.PasswordHasher;
import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleRepository;
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
    CommandLineRunner initData(
            UserService userService,
            RoleService roleService,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher,
            CategoryService categoryService) {
        return args -> {
            initUsers(userService, roleService, roleRepository, passwordHasher);
            initCategories(categoryService);
        };
    }

    private static void initUsers(
            UserService userService,
            RoleService roleService,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher) {

        if (userService.getUsers().isEmpty()) {
            Role admin = new Role("admin");
            Role user = new Role("user");

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

            roleRepository.save(admin);
            roleRepository.save(user);

            for (User basic_user : users) {
                userService.save(basic_user);
                roleService.addRoleToUser(basic_user.getId(), user.getId());
            }

            roleService.addRoleToUser(administrator.getId(), user.getId());
            roleService.addRoleToUser(administrator.getId(), admin.getId());

        }
    }

    private static void initCategories(CategoryService categoryService) {
        if (categoryService.getCategories().isEmpty()) {
            List<Category> categories = new ArrayList<>();

            categories.add(new Category("action"));
            categories.add(new Category("romance"));
            categories.add(new Category("thriller"));
            categories.add(new Category("animation"));
            categories.add(new Category("comedy"));

            categories.forEach(categoryService::saveCategory);
        }
    }
}
