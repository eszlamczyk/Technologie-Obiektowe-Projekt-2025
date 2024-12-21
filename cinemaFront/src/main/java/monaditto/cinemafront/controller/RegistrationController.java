package monaditto.cinemafront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.ControllerResource;
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
public class RegistrationController {

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
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

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
                    Platform.runLater(() -> statusLabel.setText(response));
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                    return null;
                });
    }


    @FXML
    public void loadLoginPage() {
        try {
            stageInitializer.loadStage(ControllerResource.LOGIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
        Platform.runLater(() -> statusLabel.requestFocus());
    }
}
