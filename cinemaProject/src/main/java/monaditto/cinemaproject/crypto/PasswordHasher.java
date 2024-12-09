package monaditto.cinemaproject.crypto;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class PasswordHasher {

    //yes i did hardcode pepper, problem?
    private final String PEPPER = "THE MOST SECURE PEEPER EVER!!!!1!1!1!!";

    public String hashPassword(String password) {
        try {
            String pepperedPassword = password + PEPPER;

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = md.digest(pepperedPassword.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: Hashing algorithm not found.", e);
        }
    }

}
