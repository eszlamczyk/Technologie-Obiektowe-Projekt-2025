package monaditto.cinemafront.controller.user;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.databaseMapping.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class UserMoviesController {

    private final StageInitializer stageInitializer;

    private ObservableList<MovieDto> movieDtoList;

    private final RateMovieController rateMovieController;

    @Autowired
    private MovieClientAPI movieClientAPI;

    @FXML
    private ListView<MovieDto> moviesListView;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private Button rateButton;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    public UserMoviesController(StageInitializer stageInitializer,
                                RateMovieController rateMovieController) {
        this.stageInitializer = stageInitializer;
        this.rateMovieController = rateMovieController;
    }

    @FXML
    private void initialize() {
        initializeMovieListView();
        initializeResponsiveness();
        initializeButtons();
//        loadMovies();
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

    private void initializeButtons() {
        BooleanBinding isSingleCellSelected = Bindings.createBooleanBinding(
                () -> moviesListView.getSelectionModel().getSelectedItems().size() != 1,
                moviesListView.getSelectionModel().getSelectedItems()
        );

        rateButton.disableProperty().bind(isSingleCellSelected);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchBar.getText();
        if (query.isBlank()) {
            return;
        }

        movieClientAPI.searchMovies(query)
                .thenAccept(this::updateMovieList);
//        loadMovies();
    }

    private void updateMovieList(List<MovieDto> foundMovieDtoList) {
        Platform.runLater(() -> {
            movieDtoList.clear();
            movieDtoList.addAll(foundMovieDtoList);
        });
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML void handleRate(ActionEvent event) {
        try {
            MovieDto movieDto = moviesListView.getSelectionModel().getSelectedItem();
            stageInitializer.loadStage(FXMLResourceEnum.RATE_PANEL);
            rateMovieController.setMovie(movieDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
