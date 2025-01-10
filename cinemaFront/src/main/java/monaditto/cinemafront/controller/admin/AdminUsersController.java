package monaditto.cinemafront.controller.admin;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monaditto.cinemafront.clientapi.UserClientAPI;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.UserDto;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Controller
public class AdminUsersController {

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    private final HttpClient httpClient;

    private final UserClientAPI userClientApi;

    private ObservableList<UserDto> userDtoObservableList;

    @FXML
    private ListView<UserDto> usersListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;


    public AdminUsersController(StageInitializer stageInitializer, BackendConfig backendConfig, HttpClient httpClient, UserClientAPI userClientApi) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
        this.httpClient = httpClient;
        this.userClientApi = userClientApi;
    }

    @FXML
    private void initialize() {
        userDtoObservableList = FXCollections.observableArrayList();
        usersListView.setItems(userDtoObservableList);

        usersListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(UserDto userDto, boolean empty) {
                super.updateItem(userDto, empty);
                if (empty || userDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(userDto.firstName() + " " + userDto.lastName() + " (" + userDto.email()+")");
                }
            }
        });


        loadUsers();

        deleteButton.disableProperty().bind(Bindings.isEmpty(usersListView.getSelectionModel().getSelectedItems()));
        editButton.disableProperty().bind(Bindings.isEmpty(usersListView.getSelectionModel().getSelectedItems()));
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        UserDto userDto = usersListView.getSelectionModel().getSelectedItem();
        if (userDto != null) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(backendConfig.getBaseUrl() + "/api/admin-panel/users/" + userDto.id()))
                    .DELETE()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
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

        AdminEditUserController controller = loader.getController();
        controller.init(usersListView.getSelectionModel().getSelectedItem(), this::loadUsers);

        var newScene = new Scene(newRoot);
        newStage.setTitle("Edit user");
        newStage.setScene(newScene);
        newStage.show();
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUsers() {
        userClientApi.loadUsers()
                        .thenAccept(userDtoObservableList::addAll);
    }


}
