package monaditto.cinemaproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.function.Consumer;

@Controller
public class EditUserController implements Serializable {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private UserService userService;

    private User user;

    private Runnable afterSave;

    public EditUserController() {}

    public void init(User user, Runnable afterSave, UserService userService) {
        this.user = user;
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());

        this.afterSave = afterSave;
        this.userService = userService;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        var newPassword = passwordField.getText().length() > 0 ? passwordField.getText() : null;
        var oldUserDro = new UserService.UserDto( user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword());
        var newUserDto= new UserService.UserDto(emailField.getText(), firstNameField.getText(), lastNameField.getText(),  newPassword);
        userService.editUser(oldUserDro, newUserDto);
        afterSave.run();

        Stage stage = (Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
