package monaditto.cinemafront.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.DTO.AuthResponse;
import monaditto.cinemafront.controller.DTO.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

@Controller
public class LoginController implements Initializable {

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField password;

    @FXML
    private TextField email;

    @FXML
    private Label lblLogin;

    private final StageInitializer stageInitializer;

    // Create an ObjectMapper for JSON processing
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BackendConfig backendConfig;

    @Autowired
    public LoginController(StageInitializer stageInitializer, BackendConfig backendConfig) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
    }

    @FXML
    private void login(ActionEvent event) {
        // Prepare login request payload
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(getEmail());
        loginRequest.setPassword(getPassword());

        try {
            // Create a HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Convert the loginRequest to JSON
            String requestBody = objectMapper.writeValueAsString(loginRequest);

            // Create the HttpRequest to send to the backend
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(backendConfig.getBaseUrl() + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the request asynchronously and handle the response
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            // Handle successful login (response body contains roles)
                            AuthResponse authResponse = null;
                            try {
                                authResponse = objectMapper.readValue(response.body(), AuthResponse.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            boolean isAdmin = authResponse.getRoles().stream()
                                    .anyMatch(role -> role.getName().equals("admin"));

                            if (isAdmin) {
                                Platform.runLater(() -> {
                                    try {
                                        stageInitializer.loadAdminPanelScene();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Platform.runLater(() -> lblLogin.setText("Logged in, but no admin access."));
                            }
                        } else {
                            // Handle login failure
                            Platform.runLater(() -> lblLogin.setText("Login Failed: " + response.body()));
                        }
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> lblLogin.setText("Error: " + e.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            Platform.runLater(() -> lblLogin.setText("Error: " + e.getMessage()));
        }
    }

    @FXML
    private void loadRegisterPage(MouseEvent event) {
        try {
            stageInitializer.loadRegistrationScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPassword() {
        return password.getText();
    }

    public String getEmail() {
        return email.getText();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> lblLogin.requestFocus());
    }
}
