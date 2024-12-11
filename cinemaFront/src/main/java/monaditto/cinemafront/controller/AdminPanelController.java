package monaditto.cinemafront.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
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
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.User;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Controller
public class AdminPanelController {

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private ListView<User> usersListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button signOutButton;

    public AdminPanelController(StageInitializer stageInitializer, BackendConfig backendConfig) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
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
        if (user != null) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(backendConfig.getBaseUrl() + "/api/adminPanel/users/" + user.getId()))
                    .DELETE()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenRun(this::loadUsers)
                    .exceptionally(e -> {
                        System.err.println("Error deleting user: " + e.getMessage());
                        return null;
                    });
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        var newStage = new Stage();

        var loader = new FXMLLoader(getClass().getResource("/fxml/EditUser.fxml"));
        loader.setControllerFactory(stageInitializer.getContext()::getBean);
        AnchorPane newRoot = null;
        try {
            newRoot = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EditUserController controller = loader.getController();
        controller.init(usersListView.getSelectionModel().getSelectedItem(), this::loadUsers);

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
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(backendConfig.getBaseUrl() + "/api/adminPanel/users"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseUsers)
                .thenAccept(users -> {
                    if (users != null) {
                        Platform.runLater(() -> usersListView.setItems(FXCollections.observableList(users)));
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Error loading users: " + e.getMessage());
                    return null;
                });
    }

    private List<User> parseUsers(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing roles from JSON", e);
        }
    }
}
