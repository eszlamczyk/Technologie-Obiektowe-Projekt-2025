package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.CategoryClientAPI;
import monaditto.cinemafront.clientapi.MovieRoomClientAPI;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.MovieRoomDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Optional;


@Controller
public class AdminEditMovieRoomController {

    @FXML
    public AnchorPane rootPane;
    @FXML
    public Label mainLabel;
    @FXML
    public TextField nameField;
    @FXML
    public Label statusLabel;
    @FXML
    public Button cancelButton;
    @FXML
    public Button saveButton;
    @FXML
    public Rectangle backgroundRectangle;
    @FXML
    public TextField maxSeatsField;

    @Autowired
    private MovieRoomClientAPI movieRoomClientAPI;

    private MovieRoomDto movieRoomDto;

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;


    public AdminEditMovieRoomController(StageInitializer stageInitializer, BackendConfig backendConfig){
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
    }

    @FXML
    private void initialize() {
        initializeResponsiveness();

        maxSeatsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxSeatsField.setText(oldValue);
                statusLabel.setText("Please Provide a number for max seats");
            }
        });
    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    public void setMovieRoomDto(MovieRoomDto movieRoomDto){
        mainLabel.setText("Edit Movie Room");
        this.movieRoomDto = movieRoomDto;
        nameField.setText(movieRoomDto.movieRoomName());
        maxSeatsField.setText(String.valueOf(movieRoomDto.maxSeats()));
    }

    public void resetMovieRoomDto(){
        mainLabel.setText("Add Movie Room");
        this.movieRoomDto = null;
        nameField.clear();
        maxSeatsField.clear();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_MOVIE_ROOMS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleSave(ActionEvent actionEvent) {
        if (this.movieRoomDto == null){
            createMovieRoom();
        } else {
            editMovieRoom();
        }
    }

    private void createMovieRoom() {
        Optional<MovieRoomDto> optionalMovieRoomDto = createMovieRoomDto();

        if (optionalMovieRoomDto.isEmpty()) return;

        movieRoomClientAPI.createMovieRoom(optionalMovieRoomDto.get())
                .thenAccept(responseResult -> {
                    setStatusLabelText(responseResult.body());
                    if (responseResult.statusCode() == 200) {
                        saveButton.setDisable(true);
                        handleAddSuccess();
                    }
                });

    }

    private void handleAddSuccess() {
        Platform.runLater(() -> {
            mainLabel.requestFocus();
            cancelButton.setText("Return");
        });
    }

    private void setStatusLabelText(String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }

    private Optional<MovieRoomDto> createMovieRoomDto(){
        if (nameField.getText().isEmpty()){
            statusLabel.setText("Please provide a name");
            return Optional.empty();
        }
        if (maxSeatsField.getText().isEmpty()){
            statusLabel.setText("Please provide a number of seats");
            return Optional.empty();
        }

        MovieRoomDto movieRoomDto = new MovieRoomDto(nameField.getText(), Integer.parseInt(maxSeatsField.getText()));

        return Optional.of(movieRoomDto);
    }

    private void editMovieRoom() {
        Optional<MovieRoomDto> optionalMovieRoomDto = createMovieRoomDto();
        if (optionalMovieRoomDto.isEmpty()) return;

        movieRoomClientAPI.editMovieRoom(movieRoomDto.id(), optionalMovieRoomDto.get())
                .thenAccept(responseResult -> {
                    setStatusLabelText(responseResult.body());
                    if (responseResult.statusCode() == 200){
                        handleAddSuccess();
                    }
                });
    }


}
