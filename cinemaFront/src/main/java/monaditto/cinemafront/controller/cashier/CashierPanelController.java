package monaditto.cinemafront.controller.cashier;

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
public class CashierPanelController {

    @FXML
    public Rectangle backgroundRectangle;
    @FXML
    public Button moviesButton;
    @FXML
    public Button screeningsButton;
    @FXML
    public Button purchasesButton;
    @FXML
    public Button signOutButton;

    private final StageInitializer stageInitializer;

    public AnchorPane rootPane;

    public CashierPanelController(StageInitializer stageInitializer) {
        this.stageInitializer = stageInitializer;
    }


    @FXML
    private void handleMovies() {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.CASHIER_MOVIE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleScreenings() {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.CASHIER_SCREENINGS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handlePurchases() {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.CASHIER_PURCHASES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleSignOut() {
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
