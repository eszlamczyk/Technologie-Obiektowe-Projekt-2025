package monaditto.cinemafront.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Label;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.LoginClientAPI;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.controller.DTO.AuthResponse;
import monaditto.cinemafront.databaseMapping.RoleDto;
import monaditto.cinemafront.request.RequestBuilder;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LoginHandler {
    private final LoginClientAPI loginClientAPI;
    private final ObjectMapper objectMapper;
    private final StageInitializer stageInitializer;
    private final Label lblLogin;

    private static final List<String> ROLE_PRIORITY = List.of("user", "cashier", "admin");

    private static final Map<String, FXMLResourceEnum> ROLE_TO_PANEL = Map.of(
            "admin", FXMLResourceEnum.ADMIN_PANEL,
            "cashier", FXMLResourceEnum.CASHIER_PANEL,
            "user", FXMLResourceEnum.USER_PANEL
    );

    public LoginHandler(LoginClientAPI loginClientAPI, ObjectMapper objectMapper,
                        StageInitializer stageInitializer, Label lblLogin) {
        this.loginClientAPI = loginClientAPI;
        this.objectMapper = objectMapper;
        this.stageInitializer = stageInitializer;
        this.lblLogin = lblLogin;
    }

    public void handleLogin(String email, String password) {
        loginClientAPI.login(email, password)
                .thenAccept(this::handleLoginResponse)
                .exceptionally(this::handleError);
    }

    private void handleLoginResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            processSuccessfulLogin(response);
        } else {
            updateLoginLabel("Login Failed: " + response.body());
        }
    }

    private void processSuccessfulLogin(HttpResponse<String> response) {
        try {
            RequestBuilder.extractAndSetSessionId(response);
            AuthResponse authResponse = parseAuthResponse(response);
            String userRole = determineHighestPriorityRole(authResponse.roles());
            loadAppropriatePanel(userRole);
        } catch (IOException e) {
            handleError(e);
        }
    }

    private AuthResponse parseAuthResponse(HttpResponse<String> response) throws JsonProcessingException {
        return objectMapper.readValue(response.body(), AuthResponse.class);
    }

    private String determineHighestPriorityRole(List<RoleDto> roles) {
        return roles.stream()
                .map(RoleDto::name)
                .filter(ROLE_TO_PANEL::containsKey)
                .max(Comparator.comparingInt(ROLE_PRIORITY::indexOf))
                .orElse("user");
    }

    private void loadAppropriatePanel(String role) {
        Platform.runLater(() -> {
            try {
                FXMLResourceEnum panel = ROLE_TO_PANEL.get(role);
                stageInitializer.loadStage(panel);
            } catch (IOException e) {
                handleError(e);
            }
        });
    }

    private void updateLoginLabel(String message) {
        Platform.runLater(() -> lblLogin.setText(message));
    }

    private Void handleError(Throwable e) {
        updateLoginLabel("Error: " + e.getMessage());
        return null;
    }
}