package monaditto.cinemafront.controller.admin;

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
import monaditto.cinemafront.databaseMapping.RoleDto;
import monaditto.cinemafront.databaseMapping.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminEditUserController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ListView<RoleDto> assignedRolesListView;

    @FXML
    private ListView<RoleDto> availableRolesListView;

    @FXML
    private Label statusLabel;

    @FXML
    private Button addRoleButton;

    @FXML
    private Button removeRoleButton;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient;

    private BackendConfig backendConfig;
    private UserDto userDto;
    private Runnable afterSave;

    @Autowired
    public AdminEditUserController(HttpClient httpClient, BackendConfig backendConfig) {
        this.httpClient = httpClient;
        this.backendConfig = backendConfig;
    }

    public AdminEditUserController(HttpClient httpClient){
        this.httpClient = httpClient;
    }

    @Autowired
    public void setBackendConfig(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
    }


    @FXML
    private void initialize() {
        addRoleButton.disableProperty().bind(Bindings.isEmpty(availableRolesListView.getSelectionModel().getSelectedItems()));
        removeRoleButton.disableProperty().bind(Bindings.isEmpty(assignedRolesListView.getSelectionModel().getSelectedItems()));
    }

    public void init(UserDto userDto, Runnable afterSave) {
        this.userDto = userDto;
        this.afterSave = afterSave;

        firstNameField.setText(userDto.firstName());
        lastNameField.setText(userDto.lastName());
        emailField.setText(userDto.email());

        loadRoles();
    }

    private void loadRoles() {
        Callback<ListView<RoleDto>, ListCell<RoleDto>> cellFactory = list -> new ListCell<>() {
            @Override
            protected void updateItem(RoleDto roleDto, boolean empty) {
                super.updateItem(roleDto, empty);
                if (empty || roleDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(roleDto.name());
                }
            }
        };

        assignedRolesListView.setCellFactory(cellFactory);
        availableRolesListView.setCellFactory(cellFactory);


        // Fetch assigned roles
        HttpRequest assignedRequest = HttpRequest.newBuilder()
                .uri(URI.create(backendConfig.getBaseUrl() + "/api/roles/assigned/" + userDto.id()))
                .GET()
                .build();

        httpClient.sendAsync(assignedRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseRoles)
                .thenAccept(roles -> Platform.runLater(() -> FXCollections.observableArrayList(roles).forEach(assignedRolesListView.getItems()::add)))
                .exceptionally(e -> {
                    System.err.println("Error loading assigned roles: " + e.getMessage());
                    return null;
                });

        // Fetch available roles
        HttpRequest availableRequest = HttpRequest.newBuilder()
                .uri(URI.create(backendConfig.getBaseUrl() + "/api/roles/available/" + userDto.id()))
                .GET()
                .build();

        httpClient.sendAsync(availableRequest, HttpResponse.BodyHandlers.ofString())
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

        // Serialize user details
        String newPassword = !passwordField.getText().isEmpty() ? passwordField.getText() : null;
        UserDto updatedUser = new UserDto(userDto.id(), emailField.getText(), firstNameField.getText(), lastNameField.getText(), newPassword);
        String userDtoJson = serializeUserDto(updatedUser);

        HttpRequest userUpdateRequest = HttpRequest.newBuilder()
                .uri(URI.create(backendConfig.getBaseUrl() + "/api/users/" + userDto.id()))
                .PUT(HttpRequest.BodyPublishers.ofString(userDtoJson))
                .header("Content-Type", "application/json")
                .build();

        httpClient.sendAsync(userUpdateRequest, HttpResponse.BodyHandlers.ofString())
                .thenAccept((response) -> {
                    Platform.runLater(() -> statusLabel.setText(response.body()));
                    int statusCode = response.statusCode();
                    if (statusCode != 200) {
                        throw new RuntimeException(response.body());
                    }

                    List<Long> roleIds = assignedRolesListView.getItems().stream()
                            .map(RoleDto::id)
                            .collect(Collectors.toList());
                    String roleIdsJson = serializeRoleIds(roleIds);

                    HttpRequest rolesUpdateRequest = HttpRequest.newBuilder()
                            .uri(URI.create(backendConfig.getBaseUrl() + "/api/roles/update/" + userDto.id()))
                            .POST(HttpRequest.BodyPublishers.ofString(roleIdsJson))
                            .header("Content-Type", "application/json")
                            .build();

                    httpClient.sendAsync(rolesUpdateRequest, HttpResponse.BodyHandlers.discarding())
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

    private List<RoleDto> parseRoles(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing roles from JSON", e);
        }
    }

    private String serializeUserDto(UserDto userDto) {
        try {
            return objectMapper.writeValueAsString(userDto);
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