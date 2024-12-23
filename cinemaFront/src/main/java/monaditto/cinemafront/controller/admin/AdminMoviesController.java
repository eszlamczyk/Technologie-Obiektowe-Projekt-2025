package monaditto.cinemafront.controller.admin;

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
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class AdminMoviesController {

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    private final AdminEditMovieController adminEditMovieController;

    private ObservableList<MovieDto> movieDtoList;

    @Autowired
    private MovieClientAPI movieClientAPI;

    @FXML
    private ListView<MovieDto> moviesListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    public AdminMoviesController(StageInitializer stageInitializer,
                                 BackendConfig backendConfig,
                                 AdminEditMovieController adminEditMovieController) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
        this.adminEditMovieController = adminEditMovieController;
    }

    @FXML
    private void initialize() {
        initializeMovieListView();
        initializeButtons();
        initializeResponsiveness();
        loadMovies();
    }

    private void initializeButtons() {
        BooleanBinding isSingleCellSelected = Bindings.createBooleanBinding(
                () -> moviesListView.getSelectionModel().getSelectedItems().size() != 1,
                moviesListView.getSelectionModel().getSelectedItems()
        );

        deleteButton.disableProperty().bind(isSingleCellSelected);
        editButton.disableProperty().bind(isSingleCellSelected);
    }

    private void initializeMovieListView() {
        movieDtoList = FXCollections.observableArrayList();
        moviesListView.setItems(movieDtoList);

        moviesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MovieDto movieDto, boolean empty) {
                super.updateItem(movieDto, empty);
                if (empty || movieDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(movieDto.title() + " (" + movieDto.releaseDate().getYear() + ")");
                }
            }
        });

    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    private void loadMovies() {
        movieClientAPI.loadMovies()
                .thenAccept(movieDtoList::addAll);
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        MovieDto toDelete = moviesListView.getSelectionModel().getSelectedItem();

        int status = movieClientAPI.delete(toDelete);
        if (status != 200) {
            System.err.println("Failed to delete the movie, status code = " + status);
            return;
        }
        movieDtoList.remove(toDelete);
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        try {
            MovieDto toEdit = moviesListView.getSelectionModel().getSelectedItem();
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_MOVIE);
            adminEditMovieController.setMovieDto(toEdit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_MOVIE);
            adminEditMovieController.resetMovieDto();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
