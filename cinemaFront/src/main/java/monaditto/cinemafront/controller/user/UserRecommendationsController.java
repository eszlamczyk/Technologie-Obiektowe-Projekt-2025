package monaditto.cinemafront.controller.user;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.controller.MovieCellCreator;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.MovieWithAverageRatingDto;
import monaditto.cinemafront.request.PosterDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class UserRecommendationsController {

    private final StageInitializer stageInitializer;

    private final MovieClientAPI movieClientAPI;

    private final MovieCellCreator movieCellCreator;

    private ObservableList<MovieDto> comingSoonMovieDtoList;

    @FXML
    private ListView<MovieDto> comingSoonMoviesListView;

    private ObservableList<MovieWithAverageRatingDto> recommendedMovieDtoList;

    @FXML
    private ListView<MovieWithAverageRatingDto> recommendedMoviesListView;

    private ObservableList<MovieWithAverageRatingDto> highestRatedMovieDtoList;

    @FXML
    private ListView<MovieWithAverageRatingDto> highestRatedMoviesListView;

    @FXML
    private Label recommendedMoviesEmptyLabel;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox mainHBox;

    @FXML
    private AnchorPane rootPane;

    public UserRecommendationsController(StageInitializer stageInitializer, MovieClientAPI movieClientAPI, MovieCellCreator movieCellCreator) {
        this.stageInitializer = stageInitializer;
        this.movieClientAPI = movieClientAPI;
        this.movieCellCreator = movieCellCreator;
    }

    @FXML
    private void initialize() {
        initializeMovieListViews();
        initializeResponsiveness();
        initializeButtons();

        Platform.runLater(this::loadMovies);
    }

    private void initializeMovieListViews() {
        comingSoonMovieDtoList = FXCollections.observableArrayList();
        initializeMovieListView(comingSoonMovieDtoList, comingSoonMoviesListView);
        recommendedMovieDtoList = FXCollections.observableArrayList();
        initializeMovieWithRatingsListView(recommendedMovieDtoList, recommendedMoviesListView);
        highestRatedMovieDtoList = FXCollections.observableArrayList();
        initializeMovieWithRatingsListView(highestRatedMovieDtoList, highestRatedMoviesListView);
    }

    private void initializeMovieListView(ObservableList<MovieDto> movieDtoList, ListView<MovieDto> moviesListView) {
        moviesListView.setItems(movieDtoList);

        moviesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MovieDto movieDto, boolean empty) {
                super.updateItem(movieDto, empty);
                if (empty || movieDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hBox = movieCellCreator.createMovieCell(movieDto);
                    setGraphic(hBox);
                }
            }
        });
    }


    private void initializeMovieWithRatingsListView(ObservableList<MovieWithAverageRatingDto> movieDtoList,
                                                    ListView<MovieWithAverageRatingDto> moviesListView) {
        moviesListView.setItems(movieDtoList);

        moviesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MovieWithAverageRatingDto movieWithAverageRatingDto, boolean empty) {
                super.updateItem(movieWithAverageRatingDto, empty);
                if (empty || movieWithAverageRatingDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hBox = movieCellCreator.createMovieCell(movieWithAverageRatingDto);
                    setGraphic(hBox);
                }
            }
        });
    }

    private void initializeResponsiveness() {
        scrollPane.prefHeightProperty().bind(rootPane.heightProperty());
        scrollPane.prefWidthProperty().bind(rootPane.widthProperty());

        mainHBox.prefWidthProperty().bind(rootPane.widthProperty());
    }

    private void loadMovies() {
        movieClientAPI.loadComingSoonMovies()
                .thenAccept(comingSoonMovieDtoList::addAll)
                .thenRun(() -> comingSoonMoviesListView.setPrefHeight(130 * comingSoonMovieDtoList.size()));

        movieClientAPI.loadRecommendedMovies(2L)
                .thenAccept(recommendedMovieDtoList::addAll)
                .thenRun(this::updateRecommendedMoviesView);

        movieClientAPI.loadTopRatedMovies()
                .thenAccept(highestRatedMovieDtoList::addAll)
                .thenRun(() -> highestRatedMoviesListView.setPrefHeight(130 * highestRatedMovieDtoList.size()));
    }

    private void updateRecommendedMoviesView() {
        if (recommendedMovieDtoList.isEmpty()) {
            recommendedMoviesEmptyLabel.setVisible(true);
            recommendedMoviesListView.setVisible(false);
        } else {
            recommendedMoviesEmptyLabel.setVisible(false);
            recommendedMoviesListView.setVisible(true);
            recommendedMoviesListView.setPrefHeight(130 * recommendedMovieDtoList.size());
        }
    }

    private void initializeButtons() {
//        BooleanBinding isSingleCellSelected = Bindings.createBooleanBinding(
//                () -> comingSoonMoviesListView.getSelectionModel().getSelectedItems().size() != 1,
//                comingSoonMoviesListView.getSelectionModel().getSelectedItems()
//        );
//
//        rateButton.disableProperty().bind(isSingleCellSelected);
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
