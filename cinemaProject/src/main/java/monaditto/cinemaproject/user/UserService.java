package monaditto.cinemaproject.user;

import jakarta.transaction.Transactional;
import monaditto.cinemaproject.crypto.PasswordHasher;
import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordHasher passwordHasher;

    private final UserValidator userValidator;

    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher,
            UserValidator userValidator) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
        this.userValidator = userValidator;
    }

    public Optional<UserDto> findByEmail(String email){
        return userRepository.findByEmail(email)
                .map(UserDto::userToUserDto);
    }

    public User save(User entity){
        return userRepository.save(entity);
    }

    public boolean authenticate(String email, String password){
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            return false;
        }else{
            String hashedPassword = passwordHasher.hashPassword(password);
            return hashedPassword.equals(user.get().getPassword());
        }
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::userToUserDto)
                .toList();
    }

    public CreateUserStatus editUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElse(null);
        if (existingUser == null) {
            return CreateUserStatus.MISSING_DATA;
        }

        CreateUserStatus validationStatus = userValidator.validateEditUserDto(userDto);
        if (validationStatus != CreateUserStatus.SUCCESS) {
            return validationStatus;
        }

        existingUser.setFirstName(userDto.firstName());
        existingUser.setLastName(userDto.lastName());
        existingUser.setEmail(userDto.email());

        if (userDto.password() != null) {
            String hashedPassword = passwordHasher.hashPassword(userDto.password());
            existingUser.setPassword(hashedPassword);
        }

        userRepository.save(existingUser);
        return CreateUserStatus.SUCCESS;
    }

    public CreateUserStatus createUser(UserDto userDto) {
        CreateUserStatus validationStatus = userValidator.validateCreateUserDto(userDto);
        if (validationStatus != CreateUserStatus.SUCCESS) {
            return validationStatus;
        }

        String hashedPassword = passwordHasher.hashPassword(userDto.password());
        User user = new User(
                userDto.firstName(),
                userDto.lastName(),
                userDto.email(),
                hashedPassword);
        Optional<Role> userRole = roleRepository.findByName("user");
        if (userRole.isEmpty()) {
            return CreateUserStatus.DATABASE_ERROR;
        }
        user.getRoles().add(userRole.get());
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

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<UserDto> findById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        User user = optionalUser.get();

        UserDto userDto = UserDto.userToUserDto(user);
        return Optional.of(userDto);
    }
}
