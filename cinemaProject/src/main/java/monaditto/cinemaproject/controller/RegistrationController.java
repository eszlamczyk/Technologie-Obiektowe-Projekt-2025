package monaditto.cinemaproject.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import monaditto.cinemaproject.StageInitializer;
import monaditto.cinemaproject.user.CreateUserStatus;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
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

    private final UserService userService;

    private final StageInitializer stageInitializer;

    @Autowired
    public RegistrationController(UserService userService, StageInitializer stageInitializer) {
        this.userService = userService;
        this.stageInitializer = stageInitializer;
    }

    @FXML
    public void register() {
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String password = passwordField.getText();

        UserService.UserDto userDto = new UserService.UserDto(
                email,
                firstName,
                lastName,
                password
        );

        CreateUserStatus status = userService.createUser(userDto);
        statusLabel.setText(status.message());
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
