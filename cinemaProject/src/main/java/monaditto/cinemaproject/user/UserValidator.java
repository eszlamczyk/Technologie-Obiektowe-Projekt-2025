package monaditto.cinemaproject.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CreateUserStatus validateCreateUserDto(UserDto userDto) {
        CreateUserStatus missingData = validateAllStrings(userDto);
        if (missingData != null) return missingData;

        Optional<User> optionalUser = userRepository.findByEmail(userDto.email());
        if (optionalUser.isPresent()) {
            return CreateUserStatus.USER_ALREADY_EXISTS;
        }

        if (!validateEmail(userDto.email())) {
            return CreateUserStatus.INVALID_EMAIL;
        }
        if (!validatePassword(userDto.password())) {
            return CreateUserStatus.INVALID_PASSWORD;
        }

        return CreateUserStatus.SUCCESS;
    }

    public CreateUserStatus validateEditUserDto(UserDto userDto) {

        CreateUserStatus missingData = validateAllStrings(userDto);
        if (missingData != null) return missingData;

        if (!validateEmail(userDto.email())) {
            return CreateUserStatus.INVALID_EMAIL;
        }
        if (userDto.password() != null && !validatePassword(userDto.password())) {
            return CreateUserStatus.INVALID_PASSWORD;
        }

        return CreateUserStatus.SUCCESS;
    }

    private CreateUserStatus validateAllStrings(UserDto userDto) {
        if (!validateString(userDto.firstName())
                || !validateString(userDto.lastName())
                || !validateString(userDto.email())
        ) {
            return CreateUserStatus.MISSING_DATA;
        }
        return null;
    }

    public boolean validatePassword(String password) {

        if (!validateString(password) || password.length() < 8) {
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
