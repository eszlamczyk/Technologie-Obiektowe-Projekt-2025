package monaditto.cinemaproject.user;

import jakarta.transaction.Transactional;
import monaditto.cinemaproject.crypto.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

}
