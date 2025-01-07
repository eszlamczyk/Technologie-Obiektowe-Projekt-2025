package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.PurchaseClientAPI;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.PurchaseResponseDto;
import monaditto.cinemafront.databaseMapping.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class AdminPurchasesController {
    private final StageInitializer stageInitializer;
    private final PurchaseClientAPI purchaseClientAPI;

    @FXML
    private ListView<PurchaseResponseDto> purchaseListView;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField movieTitleFilter;
    @FXML
    private ComboBox<ReservationStatus> statusFilter;
    @FXML
    private Button clearFiltersButton;

    @Autowired
    public AdminPurchasesController(StageInitializer stageInitializer,
                                    PurchaseClientAPI purchaseClientAPI) {
        this.stageInitializer = stageInitializer;
        this.purchaseClientAPI = purchaseClientAPI;
    }

    @FXML
    private void initialize() {
        setupFilters();
        setupListView();
        loadAllPurchases();
    }

    private void setupFilters() {
        movieTitleFilter.setPromptText("Filter by Movie Title");
        movieTitleFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        statusFilter.getItems().addAll(ReservationStatus.values());
        statusFilter.setPromptText("Filter by Status");
        statusFilter.setOnAction(e -> applyFilters());

        clearFiltersButton.setOnAction(e -> clearFilters());
    }

    private void setupListView() {
        purchaseListView.setCellFactory(listView -> new PurchaseListCell());
        purchaseListView.setPlaceholder(new Label("No purchases found"));
    }

    private void loadAllPurchases() {
        errorLabel.setVisible(false);

        purchaseClientAPI.loadPurchases()
                .thenAccept(purchases -> {
                    Platform.runLater(() -> {
                        purchaseListView.getItems().clear();
                        purchaseListView.getItems().addAll(purchases);
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error loading purchases: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    private void applyFilters() {
        String movieTitle = movieTitleFilter.getText().toLowerCase().trim();
        ReservationStatus selectedStatus = statusFilter.getValue();

        if (movieTitle.isEmpty() && selectedStatus == null) {
            loadAllPurchases();
            return;
        }

        errorLabel.setVisible(false);

        purchaseClientAPI.loadPurchases()
                .thenAccept(purchases -> {
                    Platform.runLater(() -> {
                        List<PurchaseResponseDto> filteredPurchases = purchases;

                        if (!movieTitle.isEmpty()) {
                            filteredPurchases = filteredPurchases.stream()
                                    .filter(p -> p.movieTitle().toLowerCase().contains(movieTitle))
                                    .toList();
                        }

                        if (selectedStatus != null) {
                            filteredPurchases = filteredPurchases.stream()
                                    .filter(p -> p.status() == selectedStatus)
                                    .toList();
                        }

                        purchaseListView.getItems().clear();
                        purchaseListView.getItems().addAll(filteredPurchases);
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error applying filters: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    private void clearFilters() {
        movieTitleFilter.clear();
        statusFilter.setValue(null);
        loadAllPurchases();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class PurchaseListCell extends ListCell<PurchaseResponseDto> {
        private final VBox content;
        private final Label titleLabel;
        private final Label detailsLabel;
        private final Label statusLabel;
        private final Label userIdLabel;

        public PurchaseListCell() {
            content = new VBox(5);
            content.setPadding(new Insets(10));
            content.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5;");

            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            detailsLabel = new Label();
            detailsLabel.setStyle("-fx-text-fill: #666;");

            statusLabel = new Label();

            userIdLabel = new Label();
            userIdLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");

            content.getChildren().addAll(titleLabel, detailsLabel, statusLabel, userIdLabel);
        }

        @Override
        protected void updateItem(PurchaseResponseDto purchase, boolean empty) {
            super.updateItem(purchase, empty);

            if (empty || purchase == null) {
                setGraphic(null);
            } else {
                titleLabel.setText(String.format(purchase.movieTitle() + " - " + purchase.screeningTime()));
                detailsLabel.setText(String.format("%d " + (purchase.boughtSeats() > 1 ? "seats" : "seat"), purchase.boughtSeats()));
                userIdLabel.setText(String.format("User ID: %d", purchase.userId()));

                statusLabel.setText("Status: " + purchase.status());
                switch (purchase.status()) {
                    case UNPAID -> statusLabel.setStyle("-fx-text-fill: #f44336;"); // Red
                    case PAID -> statusLabel.setStyle("-fx-text-fill: #4CAF50;"); // Green
                    case CANCELLED -> statusLabel.setStyle("-fx-text-fill: #9e9e9e;"); // Grey
                }

                setGraphic(content);
            }
        }
    }
}
