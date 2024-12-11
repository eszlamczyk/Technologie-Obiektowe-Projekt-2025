package monaditto.cinemafront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.config.BackendConfig;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

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

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    public RegistrationController(StageInitializer stageInitializer, BackendConfig backendConfig) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
    }

    @FXML
    public void register() {
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String password = passwordField.getText();

        String jsonRequest = String.format(
                "{\"email\":\"%s\", \"firstName\":\"%s\", \"lastName\":\"%s\", \"password\":\"%s\"}",
                email, firstName, lastName, password
        );

        String registrationUrl = backendConfig.getBaseUrl() + "/api/registration";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(registrationUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest, StandardCharsets.UTF_8))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    String message = switch (response) {
                        case "SUCCESS" -> "Successfully registered";
                        case "USER_ALREADY_EXISTS" -> "User with given email already exists";
                        case "INVALID_EMAIL" -> "Incorrect email";
                        case "INVALID_PASSWORD" -> "Incorrect password: use lowercase and uppercase letters, a number, and a special character";
                        case "MISSING_DATA" -> "Please fill up the data correctly";
                        case "DATABASE_ERROR" -> "Something went wrong in our database";
                        default -> "Unknown status";
                    };
                    Platform.runLater(() -> statusLabel.setText(message));
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                    return null;
                });
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
