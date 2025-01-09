package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.clientapi.ScreeningClientAPI;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.ScreeningDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminEditScreeningController {

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    private ScreeningDto screeningDto;

    private Map<Long, String> movieMap;

    @Autowired
    private MovieClientAPI movieClientAPI;

    @Autowired
    private ScreeningClientAPI screeningClientAPI;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private ComboBox movieField;

    @FXML
    private TextField roomField;

    @FXML
    private DatePicker startDateField;

    @FXML
    private TextField timeField;

    @FXML
    private TextField priceField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label mainLabel;

    @FXML
    private Label statusLabel;

    public AdminEditScreeningController(StageInitializer stageInitializer, BackendConfig backendConfig) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
    }

    @FXML
    private void initialize() {
        initializeResponsiveness();
        setMovieCombobox();
        timeField.setText("12:00");
        loadMovieMap();
    }

    private void loadMovieMap() {
        new Thread(() -> {
            try {
                List<MovieDto> movieDtoList = movieClientAPI.loadMovies().get();

                movieMap = movieDtoList.stream()
                        .collect(Collectors.toMap(MovieDto::id, MovieDto::title));

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("Error loading movies: " + e.getMessage());
                });
            }
        }).start();
    }

    private void setMovieCombobox() {
        movieClientAPI.loadMovies()
                .thenAccept(movies -> {
                    ObservableList<String> movieTitles = FXCollections.observableArrayList();
                    for (MovieDto movie : movies) {
                        movieTitles.add(movie.title());
                    }

                    Platform.runLater(() -> movieField.setItems(movieTitles));
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    public void setScreeningDto(ScreeningDto toEdit) {
        mainLabel.setText("Edit Screening");
        this.screeningDto = toEdit;
        loadEditedScreening();
    }

    private void loadEditedScreening() {
        movieField.setValue(movieMap.get(screeningDto.movieId()));
        startDateField.setValue(screeningDto.start().toLocalDate());
        int hour = screeningDto.start().getHour();
        int minute = screeningDto.start().getMinute();
        timeField.setText(hour + ":" + minute);
        roomField.setText(String.valueOf(screeningDto.movieRoomId()));
        priceField.setText(String.valueOf(screeningDto.price()));
    }


    public void resetScreeningDto() {

    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (screeningDto == null) {
            createScreening();
        } else {
            editScreening();
        }
    }

    private void editScreening() {
        Optional<ScreeningDto> newScreeningDto = createScreeningDto();
        if (newScreeningDto.isEmpty()) return;

        screeningClientAPI.editScreening(screeningDto.id(), newScreeningDto.get())
                .thenAccept(responseResult -> {
                    setStatusLabelText(responseResult.body());
                    if (responseResult.statusCode() == 200) {
                        handleAddSuccess();
                    }
                });
    }

    private void createScreening() {
        Optional<ScreeningDto> screeningDto = createScreeningDto();
        if (screeningDto.isEmpty()) return;

        screeningClientAPI.createScreening(screeningDto.get())
                .thenAccept(responseResult -> {
                    setStatusLabelText(responseResult.body());
                    if (responseResult.statusCode() == 200) {
                        saveButton.setDisable(true);
                        handleAddSuccess();
                    }
                });
    }

    private void setStatusLabelText(String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }

    private void handleAddSuccess() {
        Platform.runLater(() -> {
            mainLabel.requestFocus();
            cancelButton.setText("Return");
        });
    }

    private Optional<ScreeningDto> createScreeningDto() {
        try {
            String movieTitle = (String) movieField.getValue();

            Long movieId = movieMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(movieTitle))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            String[] timeParts = timeField.getText().split(":");
            if (timeParts.length != 2) {
                return Optional.empty();
            }

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            LocalDateTime start = startDateField.getValue().atTime(hour, minute);

            String roomText = roomField.getText();
            Long room = Long.parseLong(roomText);

            String priceText = priceField.getText();
            double price = Double.parseDouble(priceText);

            ScreeningDto newScreeningDto = new ScreeningDto(
                    null,
                    movieId,
                    room,
                    start,
                    price
            );

            if (movieTitle.isEmpty() || movieId == null || startDateField.getValue() == null ||
                    timeField.getText().isEmpty() || roomText.isEmpty() || priceText.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(newScreeningDto);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_SCREENINGS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
