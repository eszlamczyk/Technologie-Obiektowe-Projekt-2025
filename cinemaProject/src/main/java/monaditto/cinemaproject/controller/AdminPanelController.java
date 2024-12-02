package monaditto.cinemaproject.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monaditto.cinemaproject.StageInitializer;
import monaditto.cinemaproject.role.RoleService;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.Serializable;

@Controller
public class AdminPanelController implements Serializable {

    private final UserService userService;

    private final RoleService roleService;

    private final StageInitializer stageInitializer;

    @FXML
    private ListView<User> usersListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button signOutButton;

    @Autowired
    public AdminPanelController(UserService userService, RoleService roleService, StageInitializer stageInitializer) {
        this.userService = userService;
        this.roleService = roleService;
        this.stageInitializer = stageInitializer;
    }

    @FXML
    private void initialize() {
        usersListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(user.getFirstName() + " " + user.getLastName());
                }
            }
        });
        loadUsers();

        deleteButton.disableProperty().bind(Bindings.isEmpty(usersListView.getSelectionModel().getSelectedItems()));
        editButton.disableProperty().bind(Bindings.isEmpty(usersListView.getSelectionModel().getSelectedItems()));
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        var user = usersListView.getSelectionModel().getSelectedItem();
        userService.deleteUser(user);
        loadUsers();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        var newStage = new Stage();

        var loader = new FXMLLoader(getClass().getResource("/fxml/EditUser.fxml"));
        AnchorPane newRoot = null;
        try {
            newRoot = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EditUserController controller = loader.getController();
        controller.init(usersListView.getSelectionModel().getSelectedItem(),  this::loadUsers, userService, roleService);

        var newScene = new Scene(newRoot);
        newStage.setTitle("Edit user");
        newStage.setScene(newScene);
        newStage.show();
    }

    @FXML
    private void handleSignOut(ActionEvent event) {
        try {
            stageInitializer.loadLoginScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUsers() {
        usersListView.setItems(FXCollections.observableList(userService.getUsers()));
    }
}
