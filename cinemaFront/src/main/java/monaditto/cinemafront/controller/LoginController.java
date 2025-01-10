package monaditto.cinemafront.controller;

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
import monaditto.cinemafront.clientapi.LoginClientAPI;
import monaditto.cinemafront.controller.handler.LoginHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LoginController {

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

    private final ObjectMapper objectMapper = new ObjectMapper();


    private final LoginClientAPI loginClientAPI;
    @Autowired
    public LoginController(StageInitializer stageInitializer, LoginClientAPI loginClientAPI) {
        this.stageInitializer = stageInitializer;
        this.loginClientAPI = loginClientAPI;
    }

    @FXML
    private void login(ActionEvent event) {
        LoginHandler loginHandler = new LoginHandler(loginClientAPI, objectMapper, stageInitializer, lblLogin);
        loginHandler.handleLogin(getEmail(),getPassword());
    }

    @FXML
    private void loadRegisterPage(MouseEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.REGISTRATION);
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
