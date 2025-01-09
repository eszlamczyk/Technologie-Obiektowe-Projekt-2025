package monaditto.cinemafront.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.DTO.AuthResponse;
import monaditto.cinemafront.controller.DTO.LoginRequest;
import monaditto.cinemafront.databaseMapping.RoleDto;
import monaditto.cinemafront.request.RequestBuilder;
import monaditto.cinemafront.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Controller
public class LoginController {

    private final SessionContext sessionContext;
    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField password;

    @FXML
    private TextField email;

    @FXML
    private Label lblLogin;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    private final StageInitializer stageInitializer;

    // Create an ObjectMapper for JSON processing
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BackendConfig backendConfig;

    @Autowired
    public LoginController(StageInitializer stageInitializer, BackendConfig backendConfig, SessionContext sessionContext) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
        this.sessionContext = sessionContext;
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


            HttpRequest request =
                    RequestBuilder.buildRequestPOST(backendConfig.getBaseUrl() + "/api/auth/login", requestBody);

            System.out.println(request + requestBody);


            // Send the request asynchronously and handle the response

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            // Handle successful login (response body contains roles)
                            AuthResponse authResponse;
                            try {
                                authResponse = objectMapper.readValue(response.body(), AuthResponse.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }

                            sessionContext.setUserId(authResponse.userID());
                            sessionContext.setJwtToken(authResponse.token());

                            RequestBuilder.setJwtToken(authResponse.token());

                            //todo: add cashier
                            boolean isAdmin = authResponse.roles().stream()
                                    .anyMatch(roleDto -> roleDto.name().equals("admin"));

                            if (isAdmin) {
                                Platform.runLater(() -> {
                                    try {
                                        stageInitializer.loadStage(ControllerResource.ADMIN_PANEL);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Platform.runLater(() -> {
                                    try {
                                        stageInitializer.loadStage(ControllerResource.USER_PANEL);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
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
            stageInitializer.loadStage(ControllerResource.REGISTRATION);
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

    @FXML
    public void initialize() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
        Platform.runLater(() -> lblLogin.requestFocus());
    }
}
