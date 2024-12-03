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

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## Crypto

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## Movie

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## MovieRoom

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## Opinion

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## Purchase

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## Role

*jakiś tam opis całej sekcji*

#### Model bazodanowy

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


## Screening

*jakiś tam opis całej sekcji*

#### Model bazodanowy

#### Klasy pomocnicze

#### Warstwa serwisowa

#### Warstwa repozytorium


## User

*jakiś tam opis całej sekcji*

#### Model bazodanowy

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