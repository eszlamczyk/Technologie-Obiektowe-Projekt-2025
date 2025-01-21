package monaditto.cinemaproject;


import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.category.CategoryService;
import monaditto.cinemaproject.crypto.PasswordHasher;
import monaditto.cinemaproject.movie.CreateMovieStatus;
import monaditto.cinemaproject.movie.MovieDto;
import monaditto.cinemaproject.movie.MovieService;
import monaditto.cinemaproject.movieRoom.MovieRoomDto;
import monaditto.cinemaproject.movieRoom.MovieRoomService;
import monaditto.cinemaproject.moviedbapi.APIQuery;
import monaditto.cinemaproject.moviedbapi.MovieAPIService;
import monaditto.cinemaproject.opinion.OpinionDto;
import monaditto.cinemaproject.opinion.OpinionService;
import monaditto.cinemaproject.purchase.PurchaseDto;
import monaditto.cinemaproject.purchase.PurchaseService;
import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleRepository;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.screening.ScreeningDto;
import monaditto.cinemaproject.screening.ScreeningService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserDto;
import monaditto.cinemaproject.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Configuration
public class AppConfiguration {

    private static final List<APIQuery> releasedApiQueryList = List.of(
            new APIQuery("Avengers", 2012),
            new APIQuery("Daredevil", 2015),
            new APIQuery("Blade Runner 2049", 2017),
            new APIQuery("Indiana Jones", 0)
            );

    private static final List<APIQuery> futureApiQueryList = List.of(
            new APIQuery("Daredevil", 2025),
            new APIQuery("Captain America", 2025)
            );

    @Bean
    CommandLineRunner initData(
            UserService userService,
            RoleService roleService,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher,
            CategoryService categoryService,
            MovieService movieService,
            MovieRoomService movieRoomService,
            ScreeningService screeningService,
            OpinionService opinionService,
            MovieAPIService movieAPIService,
            PurchaseService purchaseService) {
        return args -> {
            if (userService.getUsers().isEmpty()) {
                initMovieRooms(movieRoomService);
                initCategories(categoryService);

                initUsers(userService, roleService, roleRepository, passwordHasher);
//                initMovies(movieService);
                initMoviesWithAPI(movieService, movieAPIService, releasedApiQueryList);


                addCategoriesToMovies(movieService ,categoryService);

                initScreenings(screeningService,movieService,movieRoomService);
                initOpinions(opinionService, movieService);

                initPurchases(purchaseService,userService,screeningService);
                initMoviesWithAPI(movieService, movieAPIService, futureApiQueryList);
            }
        };
    }
    private static Map<Long, Long> mapAllCategories(CategoryService categoryService){
        List<CategoryDto> categories = categoryService.getCategories();
        Map<Long, Long> categoryMap = new HashMap<>();

        for (int i = 0; i < categories.size(); i++) {
            categoryMap.put((long) i, categories.get(i).id());
        }

        return categoryMap;
    }

    private static void initMoviesWithAPI(MovieService movieService, MovieAPIService movieAPIService, List<APIQuery> apiQueryList) {

        for (APIQuery apiQuery : apiQueryList) {
            MovieDto movieDto = movieAPIService.fetchMovieByQuery(apiQuery);
            if (movieDto != null) {
                movieService.createMovie(movieDto);
            }
        }
    }

    private static void addCategoriesToMovies(MovieService movieService, CategoryService categoryService){
        Map<Long, Long> categoryMap = mapAllCategories(categoryService);
        int amountOfCategories = categoryService.getCategories().size();
        long categoryIterator1 = 1;
        long categoryIterator2 = 2;

        for (MovieDto movie : movieService.getMovies()){

            movieService.setCategories(movie.id(),
                    List.of(categoryMap.get(categoryIterator1 % amountOfCategories),
                            categoryMap.get(categoryIterator2 % amountOfCategories)));

            categoryIterator1 += 1;
            categoryIterator2 += 1;
        }

    }

    private static void initUsers(
            UserService userService,
            RoleService roleService,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher) {

        Role admin = new Role("admin");
        Role user = new Role("user");
        Role cashier = new Role("cashier");

        User administrator = new User("admin", "admin", "admin@admin.admin",
                passwordHasher.hashPassword("admin"));

        User cashman = new User("cashier", "", "cashier@cashier.cashier",
                passwordHasher.hashPassword("cashier"));

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
        userService.save(cashman);

        roleRepository.save(admin);
        roleRepository.save(user);
        roleRepository.save(cashier);

        for (User basic_user : users) {
            userService.save(basic_user);
            roleService.addRoleToUser(basic_user.getId(), user.getId());
        }

        roleService.addRoleToUser(administrator.getId(), user.getId());
        roleService.addRoleToUser(administrator.getId(), admin.getId());

        roleService.addRoleToUser(cashman.getId(), user.getId());
        roleService.addRoleToUser(cashman.getId(), cashier.getId());

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
            categories.add(new CategoryDto("musical"));
            categories.add(new CategoryDto("superhero"));
            categories.add(new CategoryDto("horror"));

            categories.forEach(categoryService::createCategory);
        }
    }

    private static void initMovies(MovieService movieService) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        initReleasedMovies(movieService, formatter);
        initUnreleasedMovies(movieService, formatter);
    }

    private static void initUnreleasedMovies(MovieService movieService, DateTimeFormatter formatter) {
        String poster;
        CreateMovieStatus movieStatus;
        List<String> categories;
        MovieDto movie;
        poster = "https://resizing.flixster.com/v7sXlfcjF1Nz014" +
                "beCSWOjGStaY=/206x305/v2/https://resizing.flixster.com/aULUdD5s" +
                "WVEoBKq8rsjqDX2T7zY=/ems.cHJkLWVtcy1hc3NldHMvdHZzZXJpZXMvYTc1N2" +
                "FhYTgtMGU0MC00YTdkLTgxMGEtZmM1MGE5NWNmMjIwLmpwZw==";
        movie = new MovieDto(
                "Daredevil: Born Again",
                "Powrót Daredevila w nowej serii.",
                120,
                poster,
                LocalDate.parse("10-03-2025", formatter));
        categories = List.of("action", "superhero");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/EA3t3rwi2u28B-nLQJ5Pt" +
                "gBKtWo=/206x305/v2/https://resizing.flixster.com/NFhs" +
                "s816If2YlSBDd2154Z6-KHw=/ems.cHJkLWVtcy1hc3NldHMvbW9" +
                "2aWVzL2UyNTU5YzhjLTI4ODktNDg2Yy1hMmM0LWY0ZDM0Y2FkNjA3Yy5qcGc=";
        movie = new MovieDto(
                "Five Nights at Freddy's 2",
                "Kontynuacja horroru o żyjących animatronikach.",
                105,
                poster,
                LocalDate.parse("30-10-2025", formatter));
        categories = List.of("horror");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/kwqhf2szJcqRBEwGq0FfyrtC34" +
                "A=/206x305/v2/https://resizing.flixste" +
                "r.com/-XZAfHZM39UwaGJIFWKAE8fS0ak=/v3/t/assets/p22289267_v_v8_aa.jpg";
        movie = new MovieDto(
                "Avatar 3",
                "James Cameron powraca do świata Pandory.",
                190,
                poster,
                LocalDate.parse("20-12-2026", formatter));
        categories = List.of("science fiction");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/B32ekw6FEC" +
                "d-_6F4QVkWeU0mVeQ=/206x305/v2/https://resizing.flixster.com/" +
                "6Ok6M-MwRvEmKWazVr_BdcNzAs0=/ems.cHJkLWVtcy1" +
                "hc3NldHMvbW92aWVzLzY3MTViMjUwLTQyZjAtNDZjZC04NmI0LWYzNTc4ZmUwMzU3Mi5qcGc=";
        movie = new MovieDto(
                "Wolf Man",
                "Horror o wilkołaku.",
                110,
                poster,
                LocalDate.parse("15-06-2025", formatter));
        categories = List.of("horror");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/AUJnbD4V-pCLZHyCFpE6dkaP" +
                "FUo=/206x305/v2/https://resizing.flixster.com/1mMlAgisc-Owh3W-" +
                "YPyT7032D4c=/ems.cHJkLWVtcy1hc3NldHMvbW92aWVzLzg5NjRjM" +
                "DE2LWFmY2EtNGE1ZC1iMDE1LTJjMWZmZWZkODhjZC5qcGc=";
        movie = new MovieDto(
                "Wish You Were Here",
                "Dramat o podróżach w czasie.",
                120,
                poster,
                LocalDate.parse("01-11-2025", formatter));
        categories = List.of("drama", "science fiction");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/gyU7YSbC0HT-wo9tB" +
                "LkE8n7JDS8=/206x305/v2/https://resizing.flixster.co" +
                "m/CY5tFqNlNhJJ3f126TgA9OOwchk=/ems.cHJkLWVtcy1h" +
                "c3NldHMvbW92aWVzLzllNjlhNTUzLWQ0YjAtNGVhYy1hYzgzLTEwZjI3NzU1OGYyMC5qcGc=";
        movie = new MovieDto(
                "Presence",
                "Thriller psychologiczny.",
                115,
                poster,
                LocalDate.parse("18-09-2025", formatter));
        categories = List.of("thriller");
        movieService.createMovieByNames(movie, categories);
    }

    private static void initReleasedMovies(MovieService movieService, DateTimeFormatter formatter) {
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
        movieService.createMovieByNames(movie, categories);

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
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/3pxO4cKBL9QjNOkV42mTU4USCMc=/20" +
                "6x305/v2/https://resizing.flixster.com/-XZAfHZM39UwaGJIFWK" +
                "AE8fS0ak=/v3/t/assets/p170620_p_v13_ae.jpg";
        movie = new MovieDto(
                "Iron Man",
                "Pierwszy film Iron Mana, początek MCU.",
                126,
                poster,
                LocalDate.parse("02-05-2008", formatter));
        categories = List.of("action", "science fiction", "superhero");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/y2qtkJifbvzjXPsZfz8NhB2fWU0=/20" +
                "6x305/v2/https://resizing.flixster.com/-XZAfHZM39UwaGJ" +
                "IFWKAE8fS0ak=/v3/t/assets/p3546118_p_v10_af.jpg";
        movie = new MovieDto(
                "Iron Man 2",
                "Kontynuacja historii Tony'ego Starka.",
                124,
                poster,
                LocalDate.parse("07-05-2010", formatter));
        categories = List.of("action", "science fiction", "superhero");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/52N-5S_PaNdR1zQAzoXk48koaT8=/206" +
                "x305/v2/https://resizing.flixster.com/-XZAfHZM39UwaG" +
                "JIFWKAE8fS0ak=/v3/t/assets/p9259486_p_v13_aa.jpg";
        movie = new MovieDto(
                "Iron Man 3",
                "Tony Stark mierzy się z Mandarynem.",
                130,
                poster,
                LocalDate.parse("03-05-2013", formatter));
        categories = List.of("action", "science fiction", "superhero");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/05F90XUPmyteSngOV7qj5Zd5H" +
                "fI=/206x305/v2/https://resizing.flixster.com/-X" +
                "ZAfHZM39UwaGJIFWKAE8fS0ak=/v3/t/assets/p24429_p_v12_bf.jpg";
        movie = new MovieDto(
                "Green Mile",
                "Poruszający dramat o więźniu z nadprzyrodzonymi zdolnościami.",
                189,
                poster,
                LocalDate.parse("10-12-1999", formatter));
        categories = List.of("drama", "fantasy");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/P7kZnOhXFWo35sqfWZnik" +
                "szbJEI=/206x305/v2/https://resizing.flixster.com/qrwaHhY7" +
                "g8sv_HP3fH-zXwyFtME=/ems.cHJkLWVtcy1hc3NldHMvbW92aWV" +
                "zL2U1ZDIyYTgzLWQxYjctNGY2MC1iNDRiLTYwZTBmOGEwOWM5Mi53ZWJw";
        movie = new MovieDto(
                "La La Land",
                "Muzyczna opowieść o miłości i marzeniach.",
                128,
                poster,
                LocalDate.parse("09-12-2016", formatter));
        categories = List.of("drama", "romance", "musical");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/fkcMkD7Ic18KDN" +
                "C73TOy6xMzA5k=/206x305/v2/https://resizing.flixster." +
                "com/U5O8tO6OHlLma7h-_hUbXeoioF0=/ems.cHJkLW" +
                "Vtcy1hc3NldHMvbW92aWVzL2E4YjU3NGQwLWY3NWQtNDk4Ni05MWMzLTlkYjY2NDZkYzVhYi5qcGc=";
        movie = new MovieDto(
                "Equalizer",
                "Denzel Washington jako mściciel wymierzający sprawiedliwość.",
                132,
                poster,
                LocalDate.parse("26-09-2014", formatter));
        categories = List.of("action", "thriller");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/iBPi8jYYfJ" +
                "gouPuxoKHMcg6vme4=/206x305/v2/https://res" +
                "izing.flixster.com/-XZAfHZM39UwaGJIFWKAE8fS" +
                "0ak=/v3/t/assets/p29821_p_v13_ai.jpg";
        movie = new MovieDto(
                "Spider-Man",
                "Peter Parker zostaje Spider-Manem.",
                121,
                poster,
                LocalDate.parse("03-05-2002", formatter));
        categories = List.of("action", "superhero");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/Rxyp_L2" +
                "fOqiEItaXust7ITlebpw=/206x305/v2/https://" +
                "resizing.flixster.com/-XZAfHZM39UwaGJIFWKA" +
                "E8fS0ak=/v3/t/assets/p7764_p_v8_au.jpg";
        movie = new MovieDto(
                "Terminator",
                "Maszyna z przyszłości poluje na Sarah Connor.",
                107,
                poster,
                LocalDate.parse("26-10-1984", formatter));
        categories = List.of("action", "science fiction");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/5R4bkJZC-W" +
                "_K-YjmIMKAXCbts5Y=/206x305/v2/https://resizing.fl" +
                "ixster.com/-XZAfHZM39UwaGJIFWKAE8fS0ak=/v3/t/assets/p2571_p_v8_aw.jpg";
        movie = new MovieDto(
                "Alien",
                "Kosmiczny horror Ridleya Scotta.",
                117,
                poster,
                LocalDate.parse("25-05-1979", formatter));
        categories = List.of("horror", "science fiction");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/kO9s-jGsOi3" +
                "YXyHkzVlmO9Z5lzI=/206x305/v2/https://resizing.flixste" +
                "r.com/hTz9Ap43sCkvDiFvCkjmb1IWkUg=/em" +
                "s.cHJkLWVtcy1hc3NldHMvbW92aWVzL2EwMGEwNmQxLTE1MGYtNGQ" +
                "wYS04ZDhlLWQ0MzYwOTQ5M2JlMC5qcGc=";
        movie = new MovieDto(
                "Matrix",
                "Neo odkrywa prawdę o rzeczywistości.",
                136,
                poster,
                LocalDate.parse("31-03-1999", formatter));
        categories = List.of("action", "science fiction");
        movieService.createMovieByNames(movie, categories);

        poster = "https://resizing.flixster.com/hMuSkvrLphf1WjBG2pmI_R" +
                "udPBU=/206x305/v2/https://resizing.flixster.com/3fd" +
                "fZluLLRSURw2fbYgsfqKaWtw=/ems.cHJkLWVtcy1hc3NldHMvb" +
                "W92aWVzLzE0ZWFjM2Y1LTYzNTYtNGIwNS1iNGU2LTk0NTA2MGQ3NjY3NC53ZWJw";
        movie = new MovieDto(
                "Drive",
                "Stylowy thriller z Ryanem Goslingiem.",
                100,
                poster,
                LocalDate.parse("16-09-2011", formatter));
        categories = List.of("action", "drama");
        movieService.createMovieByNames(movie, categories);
    }

    private static void initMovieRooms(MovieRoomService movieRoomService) {
        movieRoomService.save(new MovieRoomDto("ROOM 1", 50));
        movieRoomService.save(new MovieRoomDto("ROOM 2", 10));
        movieRoomService.save(new MovieRoomDto("ROOM 3", 20));
        movieRoomService.save(new MovieRoomDto("ROOM 4", 30));
        movieRoomService.save(new MovieRoomDto("ROOM 5", 40));
    }

    private static Map<Long, Long> mapAllMovieRooms(MovieRoomService movieRoomService){
        List<MovieRoomDto> movieRooms = movieRoomService.getAllMovieRooms();
        Map<Long, Long> categoryMap = new HashMap<>();

        for (int i = 0; i < movieRooms.size(); i++) {
            categoryMap.put((long) i, movieRooms.get(i).id());
        }

        return categoryMap;
    }

    private static void initScreenings(ScreeningService screeningService, MovieService movieService, MovieRoomService movieRoomService) {
        int movieRoomListSize = movieRoomService.getAllMovieRooms().size();
        Map<Long, Long> movieRoomsMap = mapAllMovieRooms(movieRoomService);

        List<ScreeningDto> screeningDtos = new ArrayList<>();

        Random random = new Random();

        long movieRoomIterator = 1;

        int plusDays = 1;
        int plusMinutes = 0;

        for (MovieDto movie : movieService.getMovies()){
            screeningDtos.add(new ScreeningDto(null, movie.id(), movieRoomIterator,
                    LocalDateTime.now().plusDays(plusDays).plusMinutes(plusMinutes),
                    random.nextInt(3,8) * 5.00));

            movieRoomIterator += 1;

            if (movieRoomIterator  >= movieRoomListSize){
                movieRoomIterator = 1;
                plusMinutes += 180;
                if (plusMinutes > 720){
                    plusMinutes = 0;
                    plusDays += 1;
                }
            }
        }

        for (ScreeningDto screeningDto : screeningDtos) {
            screeningService.saveScreening(screeningDto);
        }
    }

    private static void initOpinions(OpinionService opinionService, MovieService movieService) {
        List<OpinionDto> opinionDtos = new ArrayList<>();
        Random random = new Random();

        String[] comments = {
                "Świetny film!",
                "Bardzo mi się podobał.",
                "Mógłby być lepszy.",
                "Nie polecam.",
                "Rewelacyjna produkcja!"
        };

        for (MovieDto movieDto : movieService.getMovies()) {
            for (long userId = 5; userId <= 15; userId++) {
                if (movieDto.id() == 8) {
                    break;
                }

                double rating = 1 + (10 * random.nextDouble());
                String comment = comments[random.nextInt(comments.length)];

                OpinionDto opinionDto = new OpinionDto(userId, movieDto.id(), rating, comment);

                opinionDtos.add(opinionDto);
            }
        }

        for (OpinionDto opinionDto : opinionDtos) {
            opinionService.addOpinion(opinionDto);
        }
    }

    private static Map<Long, Long> mapAllUsers(UserService userService){
        List<UserDto> users = userService.getUsers();
        Map<Long, Long> userMap = new HashMap<>();

        for (int i = 0; i < users.size(); i++) {
            userMap.put((long) i, users.get(i).id());
        }

        return userMap;
    }

    private static Map<Long, Long> mapAllScreenings(ScreeningService screeningService){
        List<ScreeningDto> screenings = screeningService.getAllScreenings();
        Map<Long, Long> screeningMap = new HashMap<>();

        for (int i = 0; i < screenings.size(); i++) {
            screeningMap.put((long) i, screenings.get(i).id());
        }

        return screeningMap;
    }


    private static void initPurchases(PurchaseService purchaseService, UserService userService, ScreeningService screeningService){

        Random random = new Random();
        int amountOfUsers = userService.getUsers().size();
        int amountOfScreenings = screeningService.getAllScreenings().size();

        Map<Long,Long> usersMap = mapAllUsers(userService);
        Map<Long,Long> screeningsMap = mapAllScreenings(screeningService);

        for (int i = 0; i < 20; i++) {
            purchaseService.create(new PurchaseDto(
                            usersMap.get((long) i % amountOfUsers),
                            screeningsMap.get((long) i % amountOfScreenings),
                            random.nextInt(1,3)
                    )
            );
        }


    }
}
