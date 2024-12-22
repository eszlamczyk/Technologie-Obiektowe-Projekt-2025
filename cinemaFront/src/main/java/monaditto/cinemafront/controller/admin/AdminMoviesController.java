package monaditto.cinemafront.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.UserDto;
import monaditto.cinemafront.request.RequestBuilder;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Controller
public class AdminMoviesController {

    private final String CONTROLLER_URL;

    private final StageInitializer stageInitializer;
    private final BackendConfig backendConfig;

    private final ObjectMapper objectMapper;

    @FXML
    private ListView<MovieDto> moviesListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    public AdminMoviesController(StageInitializer stageInitializer, BackendConfig backendConfig) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
        this.CONTROLLER_URL = backendConfig.getBaseUrl() + "/api/movies";

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @FXML
    private void initialize() {
        moviesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MovieDto movieDto, boolean empty) {
                super.updateItem(movieDto, empty);
                if (empty || movieDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(movieDto.title() + " (" + movieDto.releaseDate().getYear() + ")");
                }
            }
        });

        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());

        loadMovies();
    }

    private void loadMovies() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestGET(CONTROLLER_URL);

        sendLoadMoviesRequest(client, request);
    }

    private void sendLoadMoviesRequest(HttpClient client, HttpRequest request) {
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseMovieList)
                .thenAccept(movieList -> moviesListView.setItems(FXCollections.observableList(movieList)))
                .exceptionally(e -> {
                    System.err.println("Error loading the movies: " + e.getMessage());
                    return null;
                });
    }

    @FXML
    private void handleDelete(ActionEvent event) {

    }

    @FXML
    private void handleEdit(ActionEvent event) {

    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<MovieDto> parseMovieList(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing movie list: " + e.getMessage(), e);
        }
    }

}
