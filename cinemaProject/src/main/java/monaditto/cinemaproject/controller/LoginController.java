package monaditto.cinemaproject.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import monaditto.cinemaproject.StageInitializer;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
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

    private final UserService userService;

    private final StageInitializer stageInitializer;

    @Autowired
    public LoginController(UserService userService, StageInitializer stageInitializer) {
        this.userService = userService;
        this.stageInitializer = stageInitializer;
    }

    @FXML
    private void login(ActionEvent event){
        if (userService.authenticate(getEmail(), getPassword())) {
            lblLogin.setText("Login GUT");
            var user = userService.findByEmail(getEmail());
            var isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("admin"));
            if (isAdmin) {
                try {
                    stageInitializer.loadAdminPanelScene();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            lblLogin.setText("Login Failed.");
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
