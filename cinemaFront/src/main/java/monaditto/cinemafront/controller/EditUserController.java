package monaditto.cinemafront.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.Role;
import monaditto.cinemafront.databaseMapping.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EditUserController {

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private BackendConfig backendConfig;
    private User user;
    private Runnable afterSave;

    @Autowired
    public EditUserController(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
    }

    public EditUserController(){}

    @Autowired
    public void setBackendConfig(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
    }


    @FXML
    private void initialize() {
        addRoleButton.disableProperty().bind(Bindings.isEmpty(availableRolesListView.getSelectionModel().getSelectedItems()));
        removeRoleButton.disableProperty().bind(Bindings.isEmpty(assignedRolesListView.getSelectionModel().getSelectedItems()));
    }

    public void init(User user, Runnable afterSave) {
        this.user = user;
        this.afterSave = afterSave;

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());

        loadRoles();
    }

    private void loadRoles() {
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
        availableRolesListView.setCellFactory(cellFactory);

        // Fetch available and assigned roles from the backend
        HttpClient client = HttpClient.newHttpClient();

        // Fetch assigned roles
        HttpRequest assignedRequest = HttpRequest.newBuilder()
                .uri(URI.create(backendConfig.getBaseUrl() + "/api/roles/assigned/" + user.getId()))
                .GET()
                .build();

        client.sendAsync(assignedRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseRoles)
                .thenAccept(roles -> Platform.runLater(() -> FXCollections.observableArrayList(roles).forEach(assignedRolesListView.getItems()::add)))
                .exceptionally(e -> {
                    System.err.println("Error loading assigned roles: " + e.getMessage());
                    return null;
                });

        // Fetch available roles
        HttpRequest availableRequest = HttpRequest.newBuilder()
                .uri(URI.create(backendConfig.getBaseUrl() + "/api/roles/available/" + user.getId()))
                .GET()
                .build();

        client.sendAsync(availableRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseRoles)
                .thenAccept(roles -> Platform.runLater(() -> FXCollections.observableArrayList(roles).forEach(availableRolesListView.getItems()::add)))
                .exceptionally(e -> {
                    System.err.println("Error loading available roles: " + e.getMessage());
                    return null;
                });
    }

    @FXML
    private void handleSave(ActionEvent event) {
        HttpClient client = HttpClient.newHttpClient();

        // Serialize user details
        String newPassword = !passwordField.getText().isEmpty() ? passwordField.getText() : user.getPassword();
        User updatedUser = new User(user.getId(), firstNameField.getText(), lastNameField.getText(), emailField.getText(), newPassword);
        String userJson = serializeUser(updatedUser);

        HttpRequest userUpdateRequest = HttpRequest.newBuilder()
                .uri(URI.create(backendConfig.getBaseUrl() + "/api/users/" + user.getId()))
                .PUT(HttpRequest.BodyPublishers.ofString(userJson))
                .header("Content-Type", "application/json")
                .build();

        client.sendAsync(userUpdateRequest, HttpResponse.BodyHandlers.discarding())
                .thenRun(() -> {
                    List<Long> roleIds = assignedRolesListView.getItems().stream()
                            .map(Role::getId)
                            .collect(Collectors.toList());
                    String roleIdsJson = serializeRoleIds(roleIds);

                    HttpRequest rolesUpdateRequest = HttpRequest.newBuilder()
                            .uri(URI.create(backendConfig.getBaseUrl() + "/api/roles/update/" + user.getId()))
                            .POST(HttpRequest.BodyPublishers.ofString(roleIdsJson))
                            .header("Content-Type", "application/json")
                            .build();

                    client.sendAsync(rolesUpdateRequest, HttpResponse.BodyHandlers.discarding())
                            .thenRun(() -> Platform.runLater(() -> {
                                afterSave.run();
                                closeStage(event);
                            }))
                            .exceptionally(e -> {
                                System.err.println("Error updating roles: " + e.getMessage());
                                return null;
                            });
                })
                .exceptionally(e -> {
                    System.err.println("Error updating user: " + e.getMessage());
                    return null;
                });
    }


    private void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeStage(event);
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

    private List<Role> parseRoles(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing roles from JSON", e);
        }
    }

    private String serializeUser(User user) {
        try {
            System.out.println(objectMapper.writeValueAsString(user));
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing user to JSON", e);
        }
    }

    private String serializeRoleIds(List<Long> roleIds) {
        try {
            return objectMapper.writeValueAsString(roleIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing role IDs to JSON", e);
        }
    }

}