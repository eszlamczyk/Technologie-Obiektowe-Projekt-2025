package monaditto.cinemafront.controller.cashier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.databaseMapping.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class CashierMoviesController {

    private final StageInitializer stageInitializer;

    private ObservableList<MovieDto> movieDtoList;

    @Autowired
    private MovieClientAPI movieClientAPI;

    @FXML
    private ListView<MovieDto> moviesListView;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    public CashierMoviesController(StageInitializer stageInitializer) {
        this.stageInitializer = stageInitializer;
    }

    @FXML
    private void initialize() {
        initializeMovieListView();
        initializeResponsiveness();
        loadMovies();
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
    private void handleGoBack() {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.CASHIER_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
