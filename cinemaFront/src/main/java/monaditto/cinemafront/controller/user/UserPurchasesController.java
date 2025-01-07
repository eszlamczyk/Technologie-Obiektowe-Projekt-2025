package monaditto.cinemafront.controller.user;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.PurchaseClientAPI;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.PurchaseDto;
import monaditto.cinemafront.databaseMapping.PurchaseResponseDto;
import monaditto.cinemafront.databaseMapping.ReservationStatus;
import monaditto.cinemafront.databaseMapping.ScreeningDto;
import monaditto.cinemafront.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.function.Consumer;

@Controller
public class UserPurchasesController {
    private final StageInitializer stageInitializer;
    private final SessionContext sessionContext;
    private final PurchaseClientAPI purchaseClientAPI;

    @FXML
    private ListView<PurchaseResponseDto> purchaseListView;
    @FXML
    private Label errorLabel;

    @Autowired
    public UserPurchasesController(StageInitializer stageInitializer,
                                   SessionContext sessionContext,
                                   PurchaseClientAPI purchaseClientAPI) {
        this.sessionContext = sessionContext;
        this.stageInitializer = stageInitializer;
        this.purchaseClientAPI = purchaseClientAPI;
    }

    @FXML
    private void initialize() {
        setupListView();
        loadPurchases();
    }

    private void setupListView() {
        purchaseListView.setCellFactory(listView -> new PurchaseListCell(this::handlePayment, this::handleCancellation));
        purchaseListView.setPlaceholder(new Label("No purchases found"));
    }

    private void loadPurchases() {
        errorLabel.setVisible(false);

        purchaseClientAPI.getPurchasesByUser(sessionContext.getUserId())
                .thenAccept(purchases -> {
                    Platform.runLater(() -> {
                        purchaseListView.getItems().clear();
                        purchaseListView.getItems().addAll(purchases);
                        purchaseListView.scrollTo(purchases.size() - 1);
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        errorLabel.setText("Error loading purchases: " + throwable.getMessage());
                        errorLabel.setVisible(true);
                    });
                    return null;
                });
    }

    private void handlePayment(Long purchaseId) {
        purchaseClientAPI.confirmPurchase(purchaseId)
                .thenAccept(result -> {
                    Platform.runLater(() -> {
                        if (result.statusCode() == 200) {
                            loadPurchases(); // Refresh the list
                        } else {
                            showError("Failed to process payment: " + result.body());
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() ->
                            showError("Error processing payment: " + throwable.getMessage()));
                    return null;
                });
    }

    private void handleCancellation(Long purchaseId) {
        purchaseClientAPI.cancelPurchase(purchaseId)
                .thenAccept(result -> {
                    Platform.runLater(() -> {
                        if (result.statusCode() == 200) {
                            loadPurchases(); // Refresh the list
                        } else {
                            showError("Failed to cancel purchase: " + result.body());
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() ->
                            showError("Error cancelling purchase: " + throwable.getMessage()));
                    return null;
                });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.USER_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.USER_SCREENINGS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class PurchaseListCell extends ListCell<PurchaseResponseDto> {
        private final VBox content;
        private final Label titleLabel;
        private final Label detailsLabel;
        private final Label statusLabel;
        private final HBox buttonContainer;
        private final Button payButton;
        private final Button cancelButton;
        private final Consumer<Long> onPay;
        private final Consumer<Long> onCancel;

        public PurchaseListCell(Consumer<Long> onPay, Consumer<Long> onCancel) {
            this.onPay = onPay;
            this.onCancel = onCancel;

            content = new VBox(5);
            content.setPadding(new Insets(10));
            content.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5;");

            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            detailsLabel = new Label();
            detailsLabel.setStyle("-fx-text-fill: #666;");

            statusLabel = new Label();

            buttonContainer = new HBox(10);
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);

            payButton = new Button("Pay Now");
            payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-background-radius: 15;");

            cancelButton = new Button("Cancel");
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                    "-fx-background-radius: 15;");

            buttonContainer.getChildren().addAll(cancelButton, payButton);
            content.getChildren().addAll(titleLabel, detailsLabel, statusLabel, buttonContainer);
        }

        @Override
        protected void updateItem(PurchaseResponseDto purchase, boolean empty) {
            super.updateItem(purchase, empty);

            if (empty || purchase == null) {
                setGraphic(null);
            } else {
                titleLabel.setText(String.format(purchase.movieTitle() + " - " + purchase.screeningTime()));
                detailsLabel.setText(String.format("%d " + (purchase.boughtSeats() > 1 ? "seats" : "seat"), purchase.boughtSeats()));

                statusLabel.setText("Status: " + purchase.status());
                switch (purchase.status()) {
                    case UNPAID -> {
                        statusLabel.setStyle("-fx-text-fill: #f44336;");
                        buttonContainer.setVisible(true);
                        payButton.setOnAction(e -> onPay.accept(purchase.id()));
                        cancelButton.setOnAction(e -> onCancel.accept(purchase.id()));
                    }
                    case PAID -> {
                        statusLabel.setStyle("-fx-text-fill: #4CAF50;");
                        buttonContainer.setVisible(false);
                    }
                    case CANCELLED -> {
                        statusLabel.setStyle("-fx-text-fill: #9e9e9e;");
                        buttonContainer.setVisible(false);
                    }
                }

                setGraphic(content);
            }
        }
    }
}
