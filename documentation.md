# Temat projektu
## *System do obsługi "Multipleksu kinowego"*

# Skład zespołu

- Krystian Sienkiewicz
- Krzysztof Gołuchowski
- Ernest Szlamczyk
- Mateusz Ścianek

# Wykorzystane technologie

Projekt został zaimplementowany jako aplikacja **Spring Boot** w języku *Java*. Wykorzystaliśmy bazę danych **H2**

# Schemat bazy danych

![](img/database/diagram.png)

# Model obiektowy

## Category

Tabela *categories* przechowuje kategorie, które mogą być przypisane do filmów w systemie.

#### Model bazodanowy

Klasa Category zawiera pola:
- id - ID kategorii
- categoryName - nazwa kategorii
- movies - zestaw filmów przypisanych do kategorii

Relacje:
- Relacja wiele-do-wielu z tabelą movies przez pole movies, z mapowaniem pośrednim w tabeli łączącej. Film może należeć do wielu kategorii, a każda kategoria może być przypisana do wielu filmów. Relacja jest realizowana przez tabelę pośrednią movie_category. 

```java
@Entity
@Table(name = Category.TABLE_NAME)
public class Category {

    public static final String TABLE_NAME = "categories";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @ManyToMany(mappedBy = "categories")
    private Set<Movie> movies = new HashSet<>();

    public Category() {}

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }
}
```
#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium

```java
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
```


## Crypto

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## Movie

Tabela *movies* przechowuje filmy jakie oferuje kino.

#### Model bazodanowy

Klasa Movie zawiera pola:
- id - ID filmu.
- title - tytuł filmu.
- description - opis filmu.
- duration - długość filmu w minutach.
- screenings - zestaw seansów powiązanych z filmem.
- categories - zestaw kategorii przypisanych do filmu.
- opinions - zestaw opinii użytkowników na temat filmu.

Relacje:
- Relacja jeden-do-wielu z tabelą screenings przez pole screenings: Film może mieć wiele seansów, ale każdy seans jest przypisany do jednego filmu.
- Relacja wiele-do-wielu z tabelą categories przez pole categories: Film może należeć do wielu kategorii, a każda kategoria może być przypisana do wielu filmów. Relacja jest realizowana przez tabelę pośrednią movie_category.
- Relacja jeden-do-wielu z tabelą opinions przez pole opinions: Film może mieć wiele opinii, ale każda opinia jest przypisana do jednego filmu.

```java
@Entity
@Table(name = Movie.TABLE_NAME)
public class Movie {

    public static final String TABLE_NAME = "movies";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int duration;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Screening> screenings = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_category",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Opinion> opinions;

    public Movie() {}

    public Movie(String title, String description, int duration) {
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Set<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(Set<Screening> screenings) {
        this.screenings = screenings;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(Set<Opinion> opinions) {
        this.opinions = opinions;
    }

    public void addScreening(Screening screening) {
        screenings.add(screening);
        screening.setMovie(this);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void addOpinion(Opinion opinion) {
        opinions.add(opinion);
    }
}
```

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium

```java
public interface MovieRepository extends JpaRepository<Movie,Long> {
}
```


## MovieRoom

Tabela *movie_rooms* przechowuje sale kinowe, w których wyświetlane są filmy.

#### Model bazodanowy

Klasa MovieRoom zawiera pola:
- id - ID sali kinowej.
- movieRoomName - nazwa sali kinowej.
- maxSeats - maksymalna liczba miejsc w sali.
- screenings - zestaw seansów powiązanych z tą salą.

Relacje:
- Relacja jeden-do-wielu z tabelą screenings przez pole screenings: Sala kinowa może być przypisana do wielu seansów, ale każdy seans jest związany z jedną salą kinową.

```java
@Entity
@Table(name = MovieRoom.TABLE_NAME)
public class MovieRoom {

    public static final String TABLE_NAME = "movie_rooms";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_room_id")
    private Long id;

    @Column(name = "movie_room_name", nullable = false)
    private String movieRoomName;

    @Column(name = "max_seats", nullable = false)
    private int maxSeats;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private Set<Screening> screenings = new HashSet<>();

    public MovieRoom() {}

    public MovieRoom(String movieRoomName, int maxSeats) {
        this.movieRoomName = movieRoomName;
        this.maxSeats = maxSeats;
    }

    public Long getMovieRoomId() {
        return id;
    }

    public String getMovieRoomName() {
        return movieRoomName;
    }

    public void setMovieRoomName(String movieRoomName) {
        this.movieRoomName = movieRoomName;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public Set<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(Set<Screening> screenings) {
        this.screenings = screenings;
    }

    public void addScreening(Screening screening) {
        screenings.add(screening);
        screening.setRoom(this);
    }
}
```

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium

```java
public interface MovieRoomRepository extends JpaRepository<MovieRoom, Long> {
}
```


## Opinion

Tabela *opinions* przechowuje opinie użytkoników na temat filmów.

#### Model bazodanowy

Klasa Opinion zawiera pola:
- user - użytkownik, który wystawił opinię.
- movie - film, którego dotyczy opinia.
- rating - ocena filmu w postaci liczby (np. od 1 do 10).
- comment - treść komentarza użytkownika.

Relacje:
- Relacja wiele-do-jednego z tabelą users przez pole user: Każda opinia jest przypisana do jednego użytkownika, ale użytkownik może wystawić wiele opinii.
- Relacja wiele-do-jednego z tabelą movies przez pole movie: Każda opinia dotyczy jednego filmu, ale film może mieć wiele opinii.

##### Klucz główny OpinionId

Klucz główny jest złożony, tworzony na podstawie kombinacji pól user i movie, co zapewnia, że użytkownik może wystawić tylko jedną opinię na dany film.

```java
public class OpinionId implements Serializable {

    private User user;

    private Movie movie;

    public OpinionId(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpinionId that = (OpinionId) o;
        return Objects.equals(user, that.user) && Objects.equals(movie, that.movie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, movie);
    }
}
```



```java
@Entity
@Table(name = Opinion.TABLE_NAME)
@IdClass(OpinionId.class)
public class Opinion {

    public static final String TABLE_NAME = "opinions";

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private String comment;

    public Opinion() {}

    public Opinion(User user, Movie movie, Double rating, String comment) {
        this.user = user;
        this.movie = movie;
        this.rating = rating;
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
```


#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium

```java
public interface OpinionRepository extends JpaRepository<Opinion, OpinionId> {
}
```

## Purchase

Tabela *purchases* przechowuje informacje na temat zakupów dokonanych przez użytkowników.

#### Model bazodanowy

Klasa Purchase zawiera pola:
- id - ID zakupu.
- user - użytkownik, który dokonał zakupu.
- screening - seans, na który dokonano zakupu.
- boughtSeats - liczba miejsc zakupionych przez użytkownika.
- reservationStatus - status rezerwacji

Relacje:
- Relacja wiele-do-jednego z tabelą users przez pole user: Każdy zakup jest przypisany do jednego użytkownika, ale użytkownik może mieć wiele zakupów.
- Relacja wiele-do-jednego z tabelą screenings przez pole screening: Każdy zakup dotyczy jednego seansu, ale seans może mieć wiele zakupów przypisanych do różnych użytkowników.

```java
@Entity
@Table(name = Purchase.TABLE_NAME)
public class Purchase {

    public static final String TABLE_NAME = "purchases";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "bought_seats", nullable = false)
    private int boughtSeats;

    @Column(name = "reservation_status", nullable = false)
    private String reservationStatus;

    public Purchase() {}

    public Purchase(User user, Screening screening, int boughtSeats, String reservationStatus) {
        this.user = user;
        this.screening = screening;
        this.boughtSeats = boughtSeats;
        this.reservationStatus = reservationStatus;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setScreening(Screening screening) {
        this.screening = screening;
    }

    public Screening getScreening() {
        return screening;
    }

    public void setBoughtSeats(int boughtSeats) {
        this.boughtSeats = boughtSeats;
    }

    public int getBoughtSeats() {
        return boughtSeats;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public Long getId() {
        return id;
    }
}
```

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium

```java
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
```


## Role

Tabela *roles* przechowuje role jakie mogą mieć użytkownicy (np. administrator, zwykły użytkownik).

#### Model bazodanowy

Klasa Role zawiera pola:
- id - ID roli.
- name - nazwa roli (np. "admin", "user").
- users - zestaw użytkowników przypisanych do tej roli.

Relacje:
- Relacja wiele-do-wielu z tabelą users przez pole users: Rola może być przypisana do wielu użytkowników, a każdy użytkownik może mieć przypisane wiele ról. Relacja jest realizowana przez tabelę pośrednią w encji User.

```java
@Entity
@Table(name = Role.TABLE_NAME)
public class Role {

    public static final String TABLE_NAME = "roles";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
    }
}
```


#### Klasy pomocnicze

#### Warstwa serwisowa

- `RoleService`

> Serwis obsługujący role - zarządzanie tabelą `ROLES`

Funkcjonalności:
- zwracanie wszystkich rekordów w tabeli
- tworzenie nowej roli
- dodawanie roli użytkownikowi
- usuwanie roli użytkownikowi

```java
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role createRole(String roleName) {
        var role = new Role(roleName);
        roleRepository.save(role);
        return role;
    }

    public void updateRoles(User user, Set<Role> roles) {
       user.setRoles(roles);
       userRepository.save(user);
    }

    public void addRoleToUser(User user, Role role) {
        if (!user.getRoles().contains(role)) {
            Set<Role> newRoles = new HashSet<>(user.getRoles());
            newRoles.add(role);
            user.setRoles(newRoles);
            userRepository.save(user);
        }
    }

    public void removeRoleFromUser(User user, Role role) {
        if (user.getRoles().contains(role)) {
            Set<Role> newRoles = new HashSet<>(user.getRoles());
            newRoles.remove(role);
            user.setRoles(newRoles);
            userRepository.save(user);
        }
    }
}
```

#### Warstwa repozytorium

```java
public interface RoleRepository extends JpaRepository<Role, Long> {
}
```

## Screening

Tabela *screenings* przechowuje informacje na temat konkretnego seansu.

#### Model bazodanowy

Klasa Screening zawiera pola:
- id - ID seansu.
- movie - film, który jest wyświetlany w seansie.
- room - sala kinowa, w której odbywa się seans.
- start - data i godzina rozpoczęcia seansu.
- price - cena biletu na seans.

Relacje:
- Relacja wiele-do-jednego z tabelą movies przez pole movie: Każdy seans jest związany z jednym filmem, ale film może mieć wiele seansów.
- Relacja wiele-do-jednego z tabelą movie_rooms przez pole room: Każdy seans odbywa się w jednej sali kinowej, ale sala może mieć wiele seansów.

```java
@Entity
@Table(name = Screening.TABLE_NAME)
public class Screening {

    public static final String TABLE_NAME = "screenings";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private MovieRoom room;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private Double price;

    public Screening() {}

    public Screening(Movie movie, MovieRoom room, LocalDateTime start, Double price) {
        this.movie = movie;
        this.room = room;
        this.start = start;
        this.price = price;
    }

    public Long getScreeningId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public MovieRoom getRoom() {
        return room;
    }

    public void setRoom(MovieRoom room) {
        this.room = room;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
```

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium

```java
public interface ScreeningRepository extends JpaRepository<Screening, Long> {
}
```


## User

Tabela *users* przechowuje informacje na temat użytkowników.

#### Model bazodanowy

Klasa User zawiera pola:
- id - ID użytkownika.
- email - adres email użytkownika, unikalny.
- firstName - imię użytkownika.
- lastName - nazwisko użytkownika.
- password - hasło użytkownika.
- roles - zestaw ról przypisanych do użytkownika.
- purchases - zestaw zakupów dokonanych przez użytkownika.
- opinions - zestaw opinii użytkownika o filmach.

Relacje:
- Relacja wiele-do-wielu z tabelą roles przez pole roles: Użytkownik może mieć przypisane wiele ról, a rola może być przypisana wielu użytkownikom. Relacja jest realizowana przez tabelę pośrednią user_role.
- Relacja jeden-do-wielu z tabelą purchases przez pole purchases: Użytkownik może mieć wiele zakupów, ale każdy zakup należy do jednego użytkownika.
- Relacja jeden-do-wielu z tabelą opinions przez pole opinions: Użytkownik może napisać wiele opinii o filmach, ale każda opinia należy do jednego użytkownika.

```java
@Entity
@Table(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME = "users";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Purchase> purchases = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Opinion> opinions = new HashSet<>();

    public User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName =lastName;
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
```

#### Klasy pomocnicze

- `UserValidator`

> Walidacja wprowadzanych danych użytkownika

Funkcjonalności:
- walidacja poprawności adresu email
- walidacja poprawności hasła
  - minimalna długość
  - duże i małe litery
  - cyfry
  - znak specjalny
  
```java
@Component
public class UserValidator {

    public boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialCharacters = "!@#$%^&*()-_+=<>?/|.,";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            }
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (specialCharacters.contains(String.valueOf(c))) {
                hasSpecialChar = true;
            }
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    public boolean validateEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }

    public boolean validateString(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        return !string.contains(" ");
    }
}
```

- `CreateUserStatus`

> Wynik operacji dodania użytkownika

Funkcjonalności:
- Zwracane wartości:
  - operacja zakończona sukcesem
  - operacja zakończona błędem
    - użytkownik o danym adresie email już istnieje
    - email jest niepoprawny
    - hasło nie spełnia wymagań
    - dane nie zostały w pełni wypełnione lub wypełnione błędnie
    - błąd po stronie bazy danych

```java
public enum CreateUserStatus {
    SUCCESS,
    USER_ALREADY_EXISTS,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    MISSING_DATA,
    DATABASE_ERROR;

    public String message() {
        return switch(this) {
            case SUCCESS -> "Successfully registered";
            case USER_ALREADY_EXISTS -> "User with given email already exists";
            case INVALID_EMAIL -> "Incorrect email";
            case INVALID_PASSWORD -> "Incorrect password: use lowercase and uppercase letters, a number and a special character";
            case MISSING_DATA -> "Please fill up the data correctly";
            case DATABASE_ERROR -> "Something went wrong in our database";
        };
    }
}
```

#### Warstwa serwisowa

- `UserService`

> Serwis obsługujący użytkowników - zarządzanie tabelą `USERS`

Funkcjonalności:
- zwracanie wszystkich użytkowników
- tworzenie nowego użytkownika (weryfikacja poprawności danych)
- operacja logowania użytkownika do systemu
- edycja użytkownika
- usuwanie użytkownika

`Record UserDto` - do komunikacji między serwisem a kontrolerem

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordHasher passwordHasher;

    private final UserValidator userValidator;

    @Autowired
    public UserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            UserValidator userValidator) {

        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.userValidator = userValidator;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User save(User entity){
        return userRepository.save(entity);
    }

    public boolean authenticate(String email, String password){
        User user = this.findByEmail(email);
        if (user == null){
            return false;
        }else{
            String hashedPassword = passwordHasher.hashPassword(password);
            return hashedPassword.equals(user.getPassword());
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public boolean editUser(UserDto oldUser, UserDto newUser) {
        User existingUser = userRepository.findByEmail(oldUser.email());
        if (existingUser == null) {
            return false;
        }

        existingUser.setFirstName(newUser.firstName());
        existingUser.setLastName(newUser.lastName());

        if (!userValidator.validateEmail(newUser.email)) {
            return false;
        }
        existingUser.setEmail(newUser.email());

        if (newUser.password() != null) {
            if (!userValidator.validatePassword(newUser.password())) {
                return false;
            }
            String hashedPassword = passwordHasher.hashPassword(newUser.password());
            existingUser.setPassword(hashedPassword);
        } else {
            existingUser.setPassword(oldUser.password());
        }

        userRepository.save(existingUser);
        return true;
    }

    public CreateUserStatus createUser(UserDto userDto) {
        if (!userValidator.validateString(userDto.firstName)
                || !userValidator.validateString(userDto.lastName)
                || !userValidator.validateString(userDto.email)
                || !userValidator.validateString(userDto.password)
        ) {
            return CreateUserStatus.MISSING_DATA;
        }

        System.out.println("to jest email: (" + userDto.email + ")");

        User presentUser = userRepository.findByEmail(userDto.email);
        if (presentUser != null) {
            return CreateUserStatus.USER_ALREADY_EXISTS;
        }

        if (!userValidator.validateEmail(userDto.email)) {
            return CreateUserStatus.INVALID_EMAIL;
        }
        if (!userValidator.validatePassword(userDto.password)) {
            return CreateUserStatus.INVALID_PASSWORD;
        }

        String hashedPassword = passwordHasher.hashPassword(userDto.password);
        User user = new User(
                userDto.firstName,
                userDto.lastName,
                userDto.email,
                hashedPassword);
        try {
            save(user);
            return CreateUserStatus.SUCCESS;
        } catch (Exception e) {
            return CreateUserStatus.DATABASE_ERROR;
        }
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public record UserDto(
            String email,
            String firstName,
            String lastName,
            String password
    ) {}
}
```

#### Warstwa repozytorium

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
```


# GUI

## Style CSS

`Styles.css`

```css
.btnGreen{
    -fx-background-color:  #3cbc53;
}

.background{
    -fx-background-color: DODGERBLUE;
}
```

## Rejestracja użytkownika

`Registration.fxml`

> interfejs okna rejestracji

```xml
<AnchorPane stylesheets="@../styles/Styles.css" xmlns="http://javafx.com/javafx/8.0.72"
            xmlns:fx="http://javafx.com/fxml/1" fx:controller="monaditto.cinemaproject.controller.RegistrationController">

    <Rectangle fill="CRIMSON" height="500.0" stroke="#ffffff8b" strokeType="INSIDE" width="600.0"/>
    <VBox layoutX="100" layoutY="100" spacing="20">
        <TextField fx:id="emailField" promptText="Email" prefWidth="250"/>
        <PasswordField fx:id="passwordField" promptText="Password" prefWidth="250"/>
        <TextField fx:id="firstNameField" promptText="First Name" prefWidth="250"/>
        <TextField fx:id="lastNameField" promptText="Last Name" prefWidth="250"/>

        <Button fx:id="registerButton" onAction="#register" prefHeight="40" prefWidth="150" text="Register"/>

        <Label fx:id="statusLabel" prefWidth="250" textFill="white"/>

        <Button fx:id="loginPageButton" onAction="#loadLoginPage" prefHeight="40" prefWidth="300" text="Go back to the login page"/>
    </VBox>
</AnchorPane>
```

`RegistrationController`

> Kontroler okna rejestracji

Funkcjonalności:
- wysłanie danych potrzebnych do rejestracji do `UserService`
- przejście na stronę logowania

```java
@Controller
public class RegistrationController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginPageButton;

    @FXML
    private Label statusLabel;

    private final UserService userService;

    private final StageInitializer stageInitializer;

    @Autowired
    public RegistrationController(UserService userService, StageInitializer stageInitializer) {
        this.userService = userService;
        this.stageInitializer = stageInitializer;
    }

    @FXML
    public void register() {
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String password = passwordField.getText();

        UserService.UserDto userDto = new UserService.UserDto(
                email,
                firstName,
                lastName,
                password
        );

        CreateUserStatus status = userService.createUser(userDto);
        statusLabel.setText(status.message());
    }

    @FXML
    public void loadLoginPage() {
        try {
            stageInitializer.loadLoginScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> statusLabel.requestFocus());
    }
}
```

# Aplikacja