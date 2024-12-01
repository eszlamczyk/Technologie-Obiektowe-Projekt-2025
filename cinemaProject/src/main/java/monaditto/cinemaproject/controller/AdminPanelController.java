package monaditto.cinemaproject.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
public class AdminPanelController implements Serializable {

    private final UserService userService;

    @Autowired
    public AdminPanelController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private ListView<User> usersListView;

    @FXML
    private Button deleteButton;

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
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        var user = usersListView.getSelectionModel().getSelectedItem();
        userService.deleteUser(user);
        loadUsers();
    }

    private void loadUsers() {
        usersListView.setItems(FXCollections.observableList(userService.getUsers()));
    }
}
