package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.MovieRoomClientAPI;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.MovieRoomDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class AdminMovieRoomsController {
    private final BackendConfig backendConfig;
    private final StageInitializer stageInitializer;
    private final AdminEditMovieRoomController adminEditMovieRoomController;

    @Autowired
    private MovieRoomClientAPI movieRoomClientAPI;

    private ObservableList<MovieRoomDto> movieRoomDTOs;

    @FXML
    public AnchorPane rootPane;
    @FXML
    public Rectangle backgroundRectangle;
    @FXML
    public Button editButton;
    @FXML
    public Button deleteButton;
    @FXML
    public ListView<MovieRoomDto> movieRoomsListView;

    @Autowired
    public AdminMovieRoomsController(BackendConfig backendConfig,
                                     StageInitializer stageInitializer,
                                     AdminEditMovieRoomController adminEditMovieRoomController) {
        this.backendConfig = backendConfig;
        this.stageInitializer = stageInitializer;
        this.adminEditMovieRoomController = adminEditMovieRoomController;
    }

    @FXML
    private void initialize() {
        initializeMovieListView();
        initializeButtons();
        initializeResponsiveness();
        loadMovieRooms();
    }

    private void initializeButtons() {
        BooleanBinding isSingleCellSelected = Bindings.createBooleanBinding(
                () -> movieRoomsListView.getSelectionModel().getSelectedItems().size() != 1,
                movieRoomsListView.getSelectionModel().getSelectedItems()
        );

        deleteButton.disableProperty().bind(isSingleCellSelected);
        editButton.disableProperty().bind(isSingleCellSelected);
    }

    private void initializeMovieListView() {
        movieRoomDTOs = FXCollections.observableArrayList();
        movieRoomsListView.setItems(movieRoomDTOs);

        movieRoomsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MovieRoomDto movieRoomDto, boolean empty) {
                super.updateItem(movieRoomDto, empty);
                if (empty || movieRoomDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(movieRoomDto.movieRoomName() + " (size: " + movieRoomDto.maxSeats() + ")");
                }
            }
        });

    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    private void loadMovieRooms() {
        movieRoomClientAPI.loadMovieRooms()
                .thenAccept(movieRoomDTOs::addAll);
    }


    @FXML
    public void handleAdd(ActionEvent event) {
        try{
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_MOVIE_ROOM);
            adminEditMovieRoomController.resetMovieRoomDto();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleEdit(ActionEvent event) {
        try{
            MovieRoomDto toEdit = movieRoomsListView.getSelectionModel().getSelectedItem();
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_MOVIE_ROOM);
            adminEditMovieRoomController.setMovieRoomDto(toEdit);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        MovieRoomDto toDelete = movieRoomsListView.getSelectionModel().getSelectedItem();
        movieRoomClientAPI.delete(toDelete.id())
                .thenAccept(responseResult -> {
                    if (responseResult.statusCode() != 200){
                        System.err.println("Failed to delete the category, status code = " + responseResult.statusCode());
                        return;
                    }
                    Platform.runLater(() -> movieRoomDTOs.remove(toDelete));
                });
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
