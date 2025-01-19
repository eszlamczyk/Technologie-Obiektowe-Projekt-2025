package monaditto.cinemafront.controller.user;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.clientapi.OpinionClientAPI;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.OpinionDto;
import monaditto.cinemafront.session.SessionContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserOpinionsController {
    @FXML
    private ListView<OpinionDto> opinionsListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    private List<MovieDto> movieDtoList;
    private Map<Long, MovieDto> movieMap;
    private final OpinionClientAPI opinionClientAPI;
    private final SessionContext sessionContext;
    private final StageInitializer stageInitializer;
    private final UserRateMovieController userRateMovieController;
    private final MovieClientAPI movieClientAPI;

    public UserOpinionsController(OpinionClientAPI opinionClientAPI,
                                  SessionContext sessionContext,
                                  StageInitializer stageInitializer,
                                  UserRateMovieController userRateMovieController,
                                  MovieClientAPI movieClientAPI) {
        this.opinionClientAPI = opinionClientAPI;
        this.sessionContext = sessionContext;
        this.stageInitializer = stageInitializer;
        this.userRateMovieController = userRateMovieController;
        this.movieClientAPI = movieClientAPI;
    }

    @FXML
    public void initialize() {
        loadUserOpinions();
        initializeButtons();
        initializeListView();
        loadMovieMap();
    }

    private void loadMovieMap() {
        new Thread(() -> {
            try {
                movieDtoList = movieClientAPI.loadMovies().get();

                movieMap = movieDtoList.stream()
                        .collect(Collectors.toMap(MovieDto::id, movie -> movie));

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("Error loading movies: " + e.getMessage());
                });
            }
        }).start();
    }

    private void initializeListView() {
        opinionsListView.setCellFactory(new Callback<ListView<OpinionDto>, ListCell<OpinionDto>>() {
            @Override
            public ListCell<OpinionDto> call(ListView<OpinionDto> param) {
                return new ListCell<OpinionDto>() {
                    @Override
                    protected void updateItem(OpinionDto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            MovieDto movie = movieMap.get(item.movieId());
                            BigDecimal roundedRating = BigDecimal.valueOf(item.rating()).setScale(2, RoundingMode.HALF_UP);
                            if (movie != null) {
                                setText("🎬 " + movie.title() +
                                        " | ⭐ " + roundedRating + "\n" +
                                        "💬 " + item.comment());
                            } else {
                                setText("🎬 Movie info not available | ⭐ " + roundedRating + "\n" +
                                        "💬 " + item.comment());
                            }
                        }
                    }
                };
            }
        });
    }

    private void initializeButtons() {
        BooleanBinding isSingleCellSelected = Bindings.createBooleanBinding(
                () -> opinionsListView.getSelectionModel().getSelectedItems().size() != 1,
                opinionsListView.getSelectionModel().getSelectedItems()
        );

        deleteButton.disableProperty().bind(isSingleCellSelected);
        editButton.disableProperty().bind(isSingleCellSelected);
    }

    private void loadUserOpinions() {
        opinionClientAPI.getUserOpinions(sessionContext.getUserId())
                .thenAccept(opinions -> Platform.runLater(() -> displayOpinions(opinions)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Failed to load opinions: " + ex.getMessage()));
                    return null;
                });
    }

    private void displayOpinions(List<OpinionDto> opinions) {
        opinionsListView.getItems().clear();
        if (opinions.isEmpty()) {
            opinionsListView.getItems().add(new OpinionDto(0L, 0L, 0.0, "No opinions yet..."));
        } else {
            opinionsListView.getItems().addAll(opinions);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error Loading Opinions");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleEditOpinion(ActionEvent event) {
        try {
            OpinionDto toEdit = opinionsListView.getSelectionModel().getSelectedItem();
            stageInitializer.loadStage(FXMLResourceEnum.RATE_PANEL);
            userRateMovieController.setOpinionDto(toEdit, movieMap.get(toEdit.movieId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void handleDeleteOpinion(ActionEvent event) {
        OpinionDto toDelete = opinionsListView.getSelectionModel().getSelectedItem();

        int status = opinionClientAPI.delete(toDelete);

        if (status != 200) {
            showError("Failed to delete the opinion, status code = " + status);
            return;
        }

        opinionsListView.getItems().remove(toDelete);
    }


}
