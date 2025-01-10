package monaditto.cinemafront.controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class UserPanelController {

    private final StageInitializer stageInitializer;

    @FXML
    private Button recommendedMoviesButton;

    @FXML
    private Button moviesButton;

    @FXML
    private Button screeningsButton;

    @FXML
    private Button signOutButton;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    public UserPanelController(StageInitializer stageInitializer) {
        this.stageInitializer = stageInitializer;
    }

    @FXML
    private void handleUserRecommendations(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_RECOMMENDATIONS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleUserMovies(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_MOVIE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleUserScreenings(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_SCREENINGS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleUserPurchases(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_PURCHASES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleSignOut(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.LOGIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());

        List<Button> buttons = List.of(moviesButton, screeningsButton, signOutButton);
        buttons.forEach(this::initializeButtons);
    }

    private void initializeButtons(Button button) {
        button.setOnMouseEntered(e -> button.setCursor(Cursor.HAND));
        button.setOnMouseExited(e -> button.setCursor(Cursor.DEFAULT));
    }
}
