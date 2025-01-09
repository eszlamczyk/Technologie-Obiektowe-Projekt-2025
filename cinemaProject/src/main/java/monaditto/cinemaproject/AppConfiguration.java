package monaditto.cinemaproject;

import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.category.CategoryRepository;
import monaditto.cinemaproject.category.CategoryService;
import monaditto.cinemaproject.crypto.PasswordHasher;
import monaditto.cinemaproject.movie.CreateMovieStatus;
import monaditto.cinemaproject.movie.MovieDto;
import monaditto.cinemaproject.movie.MovieRepository;
import monaditto.cinemaproject.movie.MovieService;
import monaditto.cinemaproject.movieRoom.MovieRoom;
import monaditto.cinemaproject.movieRoom.MovieRoomDto;
import monaditto.cinemaproject.movieRoom.MovieRoomService;
import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleRepository;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.screening.ScreeningDto;
import monaditto.cinemaproject.screening.ScreeningService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class AppConfiguration {

    @Bean
    CommandLineRunner initData(
            UserService userService,
            RoleService roleService,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher,
            CategoryService categoryService,
            MovieService movieService,
            MovieRoomService movieRoomService,
            ScreeningService screeningService) {
        return args -> {
            if (userService.getUsers().isEmpty()) {
                initUsers(userService, roleService, roleRepository, passwordHasher);
                initCategories(categoryService);
                initMovies(movieService);
                initMovieRooms(movieRoomService);
                initScreenings(screeningService);
            }
        };
    }

    private static void initUsers(
            UserService userService,
            RoleService roleService,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher) {

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

    private static void initCategories(CategoryService categoryService) {
        if (categoryService.getCategories().isEmpty()) {
            List<CategoryDto> categories = new ArrayList<>();

            categories.add(new CategoryDto("action"));
            categories.add(new CategoryDto("romance"));
            categories.add(new CategoryDto("thriller"));
            categories.add(new CategoryDto("science fiction"));
            categories.add(new CategoryDto("fantasy"));
            categories.add(new CategoryDto("drama"));
            categories.add(new CategoryDto("animation"));
            categories.add(new CategoryDto("comedy"));

            categories.forEach(categoryService::createCategory);
        }
    }

    private static void initMovies(MovieService movieService) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String poster = "https://resizing.flixster.com/e0yEXMKWcKU8_JJ4i3vu45poKbI" +
                "=/206x305/v2/https://resizing.flixster.com/73VCeyfsM8UDQ0o7IjUIcXr7jd8=/e" +
                "ms.cHJkLWVtcy1hc3NldHMvbW92aWVzL2RlNzI0MGQyLTQ2ZTktNGMyYi05N2VmLTFjMDhiY2VlMDQ2Ni53ZWJw";
        MovieDto movie = new MovieDto(
                "Blade Runner 2049",
                "Fajny film, literally me Ryan Gosling",
                152,
                poster,
                LocalDate.parse("06-10-2017", formatter));
        List<String> categories = List.of("science fiction", "drama");
        CreateMovieStatus movieStatus = movieService.createMovieByNames(movie, categories);
        System.out.println(movieStatus.message());

        poster = "https://resizing.flixster.com/MmthunuWaOp36uHIu8b53wT-y20=/206x305" +
                "/v2/https://resizing.flixster.com/hsDkcEPJ4iMDkn7z-MdA4S635eA=/ems.cHJkLWVtcy1" +
                "hc3NldHMvbW92aWVzL2U2ODlhNDNkLTYzNzAtNDNlZi05NjJhLTM4ZTZlNzBiOTA4ZC5qcGc=";
        movie = new MovieDto(
                "Raiders of the Lost Ark",
                "Indiana Jones tez fajny film z 1981",
                115,
                poster,
                LocalDate.parse("12-06-1981", formatter));
        categories = List.of("action");
        movieStatus = movieService.createMovieByNames(movie, categories);
        System.out.println(movieStatus.message());
    }

    private static void initMovieRooms(MovieRoomService movieRoomService) {
        movieRoomService.save(new MovieRoomDto("ROOM 1", 50));
        movieRoomService.save(new MovieRoomDto("ROOM 2", 10));
        movieRoomService.save(new MovieRoomDto("ROOM 3", 20));
        movieRoomService.save(new MovieRoomDto("ROOM 4", 30));
        movieRoomService.save(new MovieRoomDto("ROOM 5", 40));
    }

    private static void initScreenings(ScreeningService screeningService) {
        List<ScreeningDto> screeningDtos = new ArrayList<>();

        screeningDtos.add(new ScreeningDto((Long) null, 1L, 1L, LocalDateTime.now().plusDays(0), 20.00));
        screeningDtos.add(new ScreeningDto((Long) null, 2L, 2L, LocalDateTime.now().plusMinutes(50), 25.00));
        screeningDtos.add(new ScreeningDto((Long) null, 2L, 3L, LocalDateTime.now().plusDays(1), 30.00));
        screeningDtos.add(new ScreeningDto((Long) null, 2L, 4L, LocalDateTime.now().plusDays(1), 15.00));
        screeningDtos.add(new ScreeningDto((Long) null, 1L, 1L, LocalDateTime.now().plusDays(2), 20.00));
        screeningDtos.add(new ScreeningDto((Long) null, 1L, 2L, LocalDateTime.now().plusDays(2), 35.00));
        screeningDtos.add(new ScreeningDto((Long) null, 2L, 1L, LocalDateTime.now().plusDays(3), 20.00));
        screeningDtos.add(new ScreeningDto((Long) null, 1L, 2L, LocalDateTime.now().plusDays(3), 35.00));
        screeningDtos.add(new ScreeningDto((Long) null, 1L, 1L, LocalDateTime.now().plusDays(4), 20.00));
        screeningDtos.add(new ScreeningDto((Long) null, 2L, 2L, LocalDateTime.now().plusDays(4), 35.00));

        for (ScreeningDto screeningDto : screeningDtos) {
            screeningService.saveScreening(screeningDto);
        }
    }
}
