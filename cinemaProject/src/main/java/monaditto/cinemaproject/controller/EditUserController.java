package monaditto.cinemaproject.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import monaditto.cinemaproject.role.Role;
import monaditto.cinemaproject.role.RoleService;
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

    @FXML
    private ListView<Role> assignedRolesListView;

    @FXML
    private ListView<Role> availableRolesListView;

    @FXML
    private Button addRoleButton;

    @FXML
    private Button removeRoleButton;

    private UserService userService;

    private RoleService roleService;

    private User user;

    private Runnable afterSave;

    public EditUserController() {}

    @FXML
    private void initialize() {
        addRoleButton.disableProperty().bind(Bindings.isEmpty(availableRolesListView.getSelectionModel().getSelectedItems()));
        removeRoleButton.disableProperty().bind(Bindings.isEmpty(assignedRolesListView.getSelectionModel().getSelectedItems()));
    }

    public void init(User user, Runnable afterSave, UserService userService, RoleService roleService) {
        this.user = user;
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());

        Callback<ListView<Role>, ListCell<Role>> cellFactory = list -> new ListCell<>() {
            @Override
            protected void updateItem(Role role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(role.getName());
                }
            }
        };

        assignedRolesListView.setCellFactory(cellFactory);
        assignedRolesListView.setItems(FXCollections.observableArrayList(user.getRoles().stream().toList()));

        availableRolesListView.setCellFactory(cellFactory);
        var availableRoles = roleService
                .getAllRoles()
                .stream()
                .filter(role -> user.getRoles().stream().noneMatch(userRole -> userRole.getId().equals(role.getId())))
                .toList();
        availableRolesListView.setItems(FXCollections.observableArrayList(availableRoles));

        this.afterSave = afterSave;
        this.userService = userService;
        this.roleService = roleService;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        var newPassword = passwordField.getText().length() > 0 ? passwordField.getText() : null;
        var oldUserDro = new UserService.UserDto( user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword());
        var newUserDto= new UserService.UserDto(emailField.getText(), firstNameField.getText(), lastNameField.getText(),  newPassword);
        userService.editUser(oldUserDro, newUserDto);

        assignedRolesListView.getItems().forEach(role -> roleService.addRoleToUser(user, role));
        availableRolesListView.getItems().forEach(role -> roleService.removeRoleFromUser(user, role));

        afterSave.run();

        Stage stage = (Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAddRole(ActionEvent event) {
        var selectedRoles = availableRolesListView.getSelectionModel().getSelectedItems();
        assignedRolesListView.getItems().addAll(selectedRoles);
        availableRolesListView.getItems().removeAll(selectedRoles);
    }

    @FXML
    private void handleRemoveRole(ActionEvent event) {
        var selectedRoles = assignedRolesListView.getSelectionModel().getSelectedItems();
        availableRolesListView.getItems().addAll(selectedRoles);
        assignedRolesListView.getItems().removeAll(selectedRoles);
    }
}
