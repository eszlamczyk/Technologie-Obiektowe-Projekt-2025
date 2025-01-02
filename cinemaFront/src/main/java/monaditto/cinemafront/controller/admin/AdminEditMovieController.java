package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.CategoryClientAPI;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.request.PosterDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminEditMovieController {

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    private MovieDto movieDto;

    private ObservableList<CategoryDto> availableCategories;

    private ObservableList<CategoryDto> assignedCategories;

    @Autowired
    private MovieClientAPI movieClientAPI;

    @Autowired
    private CategoryClientAPI categoryClientAPI;

    @Autowired
    private PosterDownloader posterDownloader;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField durationField;

    @FXML
    private TextField releaseDateField;

    @FXML
    private TextField posterUrlField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label mainLabel;

    @FXML
    private ListView<CategoryDto> assignedCategoriesListView;

    @FXML
    private ListView<CategoryDto> availableCategoriesListView;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    public AdminEditMovieController(StageInitializer stageInitializer, BackendConfig backendConfig) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
    }

    @FXML
    private void initialize() {
        initializeResponsiveness();
        initializeCategories();
    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    private void initializeCategories() {
        Callback<ListView<CategoryDto>, ListCell<CategoryDto>> cellFactory = list -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryDto categoryDto, boolean empty) {
                super.updateItem(categoryDto, empty);
                if (empty || categoryDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(categoryDto.categoryName());
                }
            }
        };

        assignedCategoriesListView.setCellFactory(cellFactory);
        availableCategoriesListView.setCellFactory(cellFactory);

        assignedCategories = FXCollections.observableArrayList();
        availableCategories = FXCollections.observableArrayList();

        assignedCategoriesListView.setItems(assignedCategories);
        availableCategoriesListView.setItems(availableCategories);
    }

    public void setMovieDto(MovieDto movieDto) {
        mainLabel.setText("Edit movie");
        this.movieDto = movieDto;
        loadCategories();
        loadEditedMovie();
    }

    public void resetMovieDto() {
        mainLabel.setText("Add movie");
        this.movieDto = null;
        loadCategories();
        resetEditedMovie();
    }

    private void loadEditedMovie() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        titleField.setText(movieDto.title());
        descriptionField.setText(movieDto.description());
        durationField.setText(String.valueOf(movieDto.duration()));
        releaseDateField.setText(movieDto.releaseDate().format(formatter));
        posterUrlField.setText(movieDto.posterUrl());
    }

    private void resetEditedMovie() {
        titleField.clear();
        descriptionField.clear();
        durationField.clear();
        releaseDateField.clear();
        posterUrlField.clear();
    }

    private void loadCategories() {
        categoryClientAPI.loadCategories()
                .thenAccept(categoryList -> {
                    availableCategories.setAll(categoryList);
                });
        assignLoadedCategories();
    }

    private void assignLoadedCategories() {
        if (movieDto == null) {
            return;
        }
        movieClientAPI.getMovieCategories(movieDto)
                .thenAccept(movieCategories -> {
                    assignedCategories.addAll(movieCategories);
                    availableCategories.removeAll(movieCategories);
                });
    }

    private void loadPoster() {
         if (posterDownloader.isPosterUrlValid(posterUrlField.getText())) {
             Image poster = posterDownloader.getPoster();
             imageView.setImage(poster);
             statusLabel.setText("Poster loaded successfully");
         } else {
             statusLabel.setText("Invalid poster url");
         }
    }

    @FXML
    private void handleAddCategory(ActionEvent event) {
        List<CategoryDto> selectedCategories = availableCategoriesListView.getSelectionModel().getSelectedItems();
        assignedCategories.addAll(selectedCategories);
        availableCategories.removeAll(selectedCategories);
        clearCategorySelection();
    }

    @FXML
    private void handleRemoveCategory(ActionEvent event) {
        List<CategoryDto> selectedCategories = assignedCategoriesListView.getSelectionModel().getSelectedItems();
        availableCategories.addAll(selectedCategories);
        assignedCategories.removeAll(selectedCategories);
        clearCategorySelection();
    }

    private void clearCategorySelection() {
        availableCategoriesListView.getSelectionModel().clearSelection();
        assignedCategoriesListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleTestPosterUrl(ActionEvent event) {
        loadPoster();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (movieDto == null) {
            createMovie();
        } else {
            editMovie();
        }
    }

    private void createMovie() {
        Optional<MovieDto> movieDto = createMovieDto();
        if (movieDto.isEmpty()) return;

        List<CategoryDto> categories = assignedCategoriesListView.getItems();

        movieClientAPI.createMovie(movieDto.get(), categories)
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

    private Optional<MovieDto> createMovieDto() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(releaseDateField.getText(), formatter);

            MovieDto movieDto = new MovieDto(
                    null,
                    titleField.getText(),
                    descriptionField.getText(),
                    Integer.parseInt(durationField.getText()),
                    posterUrlField.getText(),
                    parsedDate);

            return Optional.of(movieDto);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid duration format.");
            return Optional.empty();
        } catch (DateTimeParseException e) {
            statusLabel.setText("Invalid date format. Use dd-MM-yyyy.");
            return Optional.empty();
        }
    }

    private void editMovie() {
        Optional<MovieDto> newMovieDto = createMovieDto();
        if (newMovieDto.isEmpty()) return;

        List<CategoryDto> categories = assignedCategoriesListView.getItems();

        movieClientAPI.editMovie(movieDto.id(), newMovieDto.get(), categories)
                .thenAccept(responseResult -> {
                    setStatusLabelText(responseResult.body());
                    if (responseResult.statusCode() == 200) {
                        handleAddSuccess();
                    }
                });
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_MOVIE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
