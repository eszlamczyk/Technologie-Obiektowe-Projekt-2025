package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.clientapi.OpinionClientAPI;
import monaditto.cinemafront.clientapi.UserClientAPI;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.OpinionDto;
import monaditto.cinemafront.databaseMapping.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Controller
public class AdminOpinionsController {
    private final StageInitializer stageInitializer;
    private final OpinionClientAPI opinionClientAPI;
    private final MovieClientAPI movieClientAPI;
    private final UserClientAPI userClientAPI;

    @FXML
    private ListView<OpinionWithDetails> opinionListView;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField userNameFilter;
    @FXML
    private TextField movieTitleFilter;
    @FXML
    private ComboBox<String> sortingOptions;
    @FXML
    private Button clearFiltersButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private List<OpinionWithDetails> allOpinions = new ArrayList<>();

    private record OpinionWithDetails(OpinionDto opinion, MovieDto movie, UserDto user) {}

    @Autowired
    public AdminOpinionsController(StageInitializer stageInitializer,
                                   OpinionClientAPI opinionClientAPI,
                                   MovieClientAPI movieClientAPI,
                                   UserClientAPI userClientAPI) {
        this.stageInitializer = stageInitializer;
        this.opinionClientAPI = opinionClientAPI;
        this.movieClientAPI = movieClientAPI;
        this.userClientAPI = userClientAPI;
    }

    @FXML
    private void initialize() {
        setupFilters();
        setupListView();
        loadAllOpinions();
    }

    private void setupFilters() {
        userNameFilter.setPromptText("Filter by User Name");
        userNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        movieTitleFilter.setPromptText("Filter by Movie Title");
        movieTitleFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        sortingOptions.getItems().addAll(
                "Highest Rating",
                "Lowest Rating",
                "User Name (A-Z)",
                "User Name (Z-A)"
        );
        sortingOptions.setPromptText("Sort by");
        sortingOptions.setOnAction(e -> applyFilters());

        clearFiltersButton.setOnAction(e -> clearFilters());
    }

    private void setupListView() {
        opinionListView.setCellFactory(listView -> new OpinionListCell(
                this::handleEdit,
                this::handleDelete
        ));
        opinionListView.setPlaceholder(new Label("No opinions found"));
    }

    private void loadAllOpinions() {
        errorLabel.setVisible(false);
        loadingIndicator.setVisible(true);

        CompletableFuture<Map<Long, MovieDto>> moviesFuture = movieClientAPI.loadMovies()
                .thenApply(movies -> movies.stream()
                        .collect(Collectors.toMap(MovieDto::id, movie -> movie)));

        CompletableFuture<Map<Long, UserDto>> usersFuture = userClientAPI.loadUsers()
                .thenApply(users -> users.stream()
                        .collect(Collectors.toMap(UserDto::id, user -> user)));

        CompletableFuture<List<OpinionDto>> opinionsFuture = opinionClientAPI.getAllOpinions();

        CompletableFuture.allOf(moviesFuture, usersFuture, opinionsFuture)
                .thenRun(() -> {
                    try {
                        Map<Long, MovieDto> movies = moviesFuture.get();
                        Map<Long, UserDto> users = usersFuture.get();
                        List<OpinionDto> opinions = opinionsFuture.get();

                        List<OpinionWithDetails> opinionDetails = opinions.stream()
                                .filter(opinion -> movies.containsKey(opinion.movieId()) && users.containsKey(opinion.userId()))
                                .map(opinion -> new OpinionWithDetails(
                                        opinion,
                                        movies.get(opinion.movieId()),
                                        users.get(opinion.userId())
                                ))
                                .toList();

                        Platform.runLater(() -> {
                            allOpinions = new ArrayList<>(opinionDetails);
                            opinionListView.getItems().clear();
                            opinionListView.getItems().addAll(opinionDetails);
                            loadingIndicator.setVisible(false);
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showError("Error loading data: " + e.getMessage());
                            loadingIndicator.setVisible(false);
                        });
                    }
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error loading opinions: " + throwable.getMessage());
                        loadingIndicator.setVisible(false);
                    });
                    return null;
                });
    }

    private void handleEdit(OpinionWithDetails opinionDetails) {
        Dialog<OpinionDto> dialog = new Dialog<>();
        dialog.setTitle("Edit Opinion");
        dialog.setHeaderText(String.format("Edit opinion by %s %s for movie: %s",
                opinionDetails.user().firstName(),
                opinionDetails.user().lastName(),
                opinionDetails.movie().title()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea commentArea = new TextArea(opinionDetails.opinion().comment());
        Spinner<Double> ratingSpinner = new Spinner<>(1.0, 5.0, opinionDetails.opinion().rating(), 0.5);

        grid.add(new Label("Rating:"), 0, 0);
        grid.add(ratingSpinner, 1, 0);
        grid.add(new Label("Comment:"), 0, 1);
        grid.add(commentArea, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new OpinionDto(
                        opinionDetails.opinion().userId(),
                        opinionDetails.opinion().movieId(),
                        ratingSpinner.getValue(),
                        commentArea.getText()
                );
            }
            return null;
        });

        Optional<OpinionDto> result = dialog.showAndWait();
        result.ifPresent(updatedOpinion -> {
            opinionClientAPI.editOpinion(updatedOpinion)
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                loadAllOpinions();
                            } else {
                                showError("Failed to update opinion: " + response.body());
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() ->
                                showError("Error updating opinion: " + throwable.getMessage())
                        );
                        return null;
                    });
        });
    }

    private void handleDelete(OpinionWithDetails opinionDetails) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Opinion");
        confirmDialog.setContentText(String.format("Are you sure you want to delete %s %s's opinion for %s?",
                opinionDetails.user().firstName(),
                opinionDetails.user().lastName(),
                opinionDetails.movie().title()));

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int statusCode = opinionClientAPI.delete(opinionDetails.opinion());
            if (statusCode == 200) {
                loadAllOpinions();
            } else {
                showError("Failed to delete opinion");
            }
        }
    }

    private void applyFilters() {
        String userName = userNameFilter.getText().toLowerCase().trim();
        String movieTitle = movieTitleFilter.getText().toLowerCase().trim();
        String sortOption = sortingOptions.getValue();

        List<OpinionWithDetails> filteredOpinions = new ArrayList<>(allOpinions);

        if (!userName.isEmpty()) {
            filteredOpinions = filteredOpinions.stream()
                    .filter(o -> {
                        String fullName = (o.user().firstName() + " " + o.user().lastName()).toLowerCase();
                        return fullName.contains(userName);
                    })
                    .collect(Collectors.toList());
        }

        if (!movieTitle.isEmpty()) {
            filteredOpinions = filteredOpinions.stream()
                    .filter(o -> o.movie().title().toLowerCase().contains(movieTitle))
                    .collect(Collectors.toList());
        }

        if (sortOption != null) {
            switch (sortOption) {
                case "Highest Rating" ->
                        filteredOpinions.sort((o1, o2) ->
                                o2.opinion().rating().compareTo(o1.opinion().rating()));
                case "Lowest Rating" ->
                        filteredOpinions.sort(Comparator.comparing(o -> o.opinion().rating()));
                case "User Name (A-Z)" ->
                        filteredOpinions.sort((o1, o2) -> {
                            String name1 = o1.user().firstName() + " " + o1.user().lastName();
                            String name2 = o2.user().firstName() + " " + o2.user().lastName();
                            return name1.compareToIgnoreCase(name2);
                        });
                case "User Name (Z-A)" ->
                        filteredOpinions.sort((o1, o2) -> {
                            String name1 = o1.user().firstName() + " " + o1.user().lastName();
                            String name2 = o2.user().firstName() + " " + o2.user().lastName();
                            return name2.compareToIgnoreCase(name1);
                        });
            }
        }

        opinionListView.getItems().clear();
        opinionListView.getItems().addAll(filteredOpinions);
    }

    private void clearFilters() {
        userNameFilter.clear();
        movieTitleFilter.clear();
        sortingOptions.setValue(null);
        loadAllOpinions();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class OpinionListCell extends ListCell<OpinionWithDetails> {
        private final VBox content;
        private final Label titleLabel;
        private final Label userLabel;
        private final Label ratingLabel;
        private final Label commentLabel;
        private final HBox buttonContainer;

        public OpinionListCell(Consumer<OpinionWithDetails> onEdit,
                               Consumer<OpinionWithDetails> onDelete) {
            content = new VBox(5);
            content.setPadding(new Insets(10));
            content.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5;");

            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            userLabel = new Label();
            userLabel.setStyle("-fx-text-fill: #666;");

            ratingLabel = new Label();
            ratingLabel.setStyle("-fx-font-weight: bold;");

            commentLabel = new Label();
            commentLabel.setWrapText(true);

            buttonContainer = new HBox(10);
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);

            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-background-radius: 15;");

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                    "-fx-background-radius: 15;");

            buttonContainer.getChildren().addAll(deleteButton, editButton);
            content.getChildren().addAll(titleLabel, userLabel, ratingLabel,
                    commentLabel, buttonContainer);

            editButton.setOnAction(e -> onEdit.accept(getItem()));
            deleteButton.setOnAction(e -> onDelete.accept(getItem()));
        }

        @Override
        protected void updateItem(OpinionWithDetails details, boolean empty) {
            super.updateItem(details, empty);

            if (empty || details == null) {
                setGraphic(null);
            } else {
                titleLabel.setText(details.movie().title());
                userLabel.setText(String.format("User: %s %s (%s)",
                        details.user().firstName(),
                        details.user().lastName(),
                        details.user().email()));
                ratingLabel.setText("Rating: " + new DecimalFormat("0.00").format(details.opinion.rating()));
                commentLabel.setText(details.opinion().comment());
                setGraphic(content);
            }
        }
    }
}
