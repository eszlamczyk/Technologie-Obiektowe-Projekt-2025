package monaditto.cinemaproject.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    private final PasswordHasher passwordHasher;

    @Autowired
    public CustomPasswordEncoder(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }


    @Override
    public String encode(CharSequence rawPassword) {
        return passwordHasher.hashPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
