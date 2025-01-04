package monaditto.cinemafront.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.StageInitializer;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class AdminPanelController {

    private final StageInitializer stageInitializer;


    @FXML
    private Button usersButton;

    @FXML
    private Button moviesButton;

    @FXML
    private Button screeningsButton;

    @FXML
    public Button categoriesButton;

    @FXML
    public Button movieRoomsButton;

    @FXML
    private Button signOutButton;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    public AdminPanelController(StageInitializer stageInitializer) {
        this.stageInitializer = stageInitializer;
    }

    @FXML
    private void handleAdminUsers(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_USER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleAdminMovies(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_MOVIE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleScreenings(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_SCREENINGS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleCategories(ActionEvent actionEvent) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_CATEGORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void handleMovieRooms(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_MOVIE_ROOMS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleSignOut(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.LOGIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void initialize() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());

        List<Button> buttons = List.of(usersButton, moviesButton, screeningsButton, categoriesButton,
                movieRoomsButton, signOutButton);
        buttons.forEach(this::initializeButtons);
    }

    private void initializeButtons(Button button) {
        button.setOnMouseEntered(e -> button.setCursor(Cursor.HAND));
        button.setOnMouseExited(e -> button.setCursor(Cursor.DEFAULT));
    }



}
