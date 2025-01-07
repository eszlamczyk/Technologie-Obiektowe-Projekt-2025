package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.PurchaseClientAPI;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.PurchaseDto;
import monaditto.cinemafront.databaseMapping.ReservationStatus;
import monaditto.cinemafront.databaseMapping.ScreeningDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class AdminBuyTicketsController {

    private StageInitializer stageInitializer;

    private PurchaseClientAPI purchaseClientAPI;

    private ScreeningDto screeningDto;

    @FXML
    private Label movieNameLabel;

    @FXML
    private Label movieTimeLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField numOfSeatsField;

    @Autowired
    public AdminBuyTicketsController(StageInitializer stageInitializer, PurchaseClientAPI purchaseClientAPI) {
        this.stageInitializer = stageInitializer;
        this.purchaseClientAPI = purchaseClientAPI;
    }

    @FXML
    private void initialize() {
        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numOfSeatsField.setText(newValue.replaceAll("\\D", ""));
            } else if (!newValue.isEmpty()) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value < 1) {
                        numOfSeatsField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    numOfSeatsField.setText(oldValue);
                }
            }
        };
        //userIdField.textProperty().addListener(listener);
        numOfSeatsField.textProperty().addListener(listener);
        numOfSeatsField.setText("1");
    }

    public void setScreeningDto(ScreeningDto screeningDto) {
        this.screeningDto = screeningDto;
        this.movieTimeLabel.setText("Starting at " + screeningDto.start());
    }

    public void setMovieName(String movieName) {
        this.movieNameLabel.setText("Movie name: " + movieName);
    }

    @FXML
    public void handleBuy(ActionEvent event) {
        Long userId = Long.valueOf(userIdField.getText());
        int numOfSeats = Integer.parseInt(numOfSeatsField.getText());
        var purchaseDto = new PurchaseDto(1L, userId, screeningDto.id(), numOfSeats, ReservationStatus.UNPAID);
        purchaseClientAPI.createPurchase(purchaseDto)
                .thenAccept(result -> {
                    Platform.runLater(() -> {
                        switch (result.statusCode()) {
                            case 200 -> {
                                try {
                                    stageInitializer.loadStage(ControllerResource.ADMIN_PURCHASES);
                                } catch (IOException e) {
                                    showError("Error navigating to screenings page: " + e.getMessage());
                                }
                            }
                            case 400 -> showError("Invalid purchase request. Please check your inputs.");
                            case 409 -> showError("Cannot complete purchase. Seats might not be available.");
                            case 500 -> showError("Server error. Please try again later.");
                            default -> showError("Unexpected error occurred. Please try again.");
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() ->
                            showError("Connection error: " + throwable.getMessage()));
                    return null;
                });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_SCREENINGS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
