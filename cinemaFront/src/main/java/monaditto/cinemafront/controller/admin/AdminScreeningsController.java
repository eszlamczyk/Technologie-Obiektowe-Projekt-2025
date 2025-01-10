package monaditto.cinemafront.controller.admin;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.clientapi.ScreeningClientAPI;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.ControllerResource;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.ScreeningDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminScreeningsController {

    private final StageInitializer stageInitializer;

    private final BackendConfig backendConfig;

    private final AdminEditScreeningController adminEditScreeningController;

    private ObservableList<ScreeningDto> screeningsDtoList;

    private List<ScreeningDto> allScreenings;

    private List<MovieDto> movieDtoList;

    private Map<Long, String> movieMap;

    @Autowired
    private ScreeningClientAPI screeningClientAPI;

    @Autowired
    private MovieClientAPI movieClientAPI;

    @Autowired
    private AdminBuyTicketsController adminBuyTicketsController;

    @FXML
    private ListView<ScreeningDto> screeningsListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Rectangle backgroundRectangle;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button buttonToday;

    @FXML
    private Button buttonTomorrow;

    @FXML
    private Button buttonPlusTwo;

    @FXML
    private Button buttonPlusThree;

    @FXML
    private Button buttonPlusFour;

    @FXML
    private Button buyButton;

    public AdminScreeningsController(StageInitializer stageInitializer,
                                 BackendConfig backendConfig,
                                 AdminEditScreeningController adminEditScreeningController) {
        this.stageInitializer = stageInitializer;
        this.backendConfig = backendConfig;
        this.adminEditScreeningController = adminEditScreeningController;
    }

    @FXML
    private void initialize() {
        allScreenings = new ArrayList<>();
        initializeScreeningListView();
        initializeButtons();
        initializeResponsiveness();
        loadScreenings();
        initializeDayButtons();
    }

    private void initializeButtons() {
        BooleanBinding isSingleCellSelected = Bindings.createBooleanBinding(
                () -> screeningsListView.getSelectionModel().getSelectedItems().size() != 1,
                screeningsListView.getSelectionModel().getSelectedItems()
        );

        deleteButton.disableProperty().bind(isSingleCellSelected);
        editButton.disableProperty().bind(isSingleCellSelected);
        buyButton.disableProperty().bind(isSingleCellSelected);
    }

    private void initializeScreeningListView() {
        screeningsDtoList = FXCollections.observableArrayList();
        screeningsListView.setItems(screeningsDtoList);

        screeningsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ScreeningDto screeningDto, boolean empty) {
                super.updateItem(screeningDto, empty);
                if (empty || screeningDto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm");
                    String formattedDate = screeningDto.start().format(formatter);

                    String movieName = movieMap.get(screeningDto.movieId());
                    LocalDateTime date = screeningDto.start();
                    setText(movieName + " in room: " + screeningDto.movieRoomId() + ". Start: " + formattedDate);
                }
            }
        });

    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    private void loadScreenings() {
        loadMovieMap();
        screeningClientAPI.loadScreenings()
                .thenAccept(screeningDtos -> {
                    screeningsDtoList.addAll(screeningDtos);
                    allScreenings.addAll(screeningDtos);
                });
    }

    private void loadMovieMap() {
        new Thread(() -> {
            try {
                movieDtoList = movieClientAPI.loadMovies().get();

                movieMap = movieDtoList.stream()
                        .collect(Collectors.toMap(MovieDto::id, MovieDto::title));

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("Error loading movies: " + e.getMessage());
                });
            }
        }).start();
    }

    private void initializeDayButtons() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate plusTwo = today.plusDays(2);
        LocalDate plusThree = today.plusDays(3);
        LocalDate plusFour = today.plusDays(4);

        buttonToday.setText(today.format(DateTimeFormatter.ofPattern("dd.MM")));
        buttonTomorrow.setText(tomorrow.format(DateTimeFormatter.ofPattern("dd.MM")));
        buttonPlusTwo.setText(plusTwo.format(DateTimeFormatter.ofPattern("dd.MM")));
        buttonPlusThree.setText(plusThree.format(DateTimeFormatter.ofPattern("dd.MM")));
        buttonPlusFour.setText(plusFour.format(DateTimeFormatter.ofPattern("dd.MM")));
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        ScreeningDto toDelete = screeningsListView.getSelectionModel().getSelectedItem();

        int status = screeningClientAPI.delete(toDelete);
        if (status != 200) {
            System.err.println("Failed to delete the screening, status code = " + status);
            return;
        }
        screeningsDtoList.remove(toDelete);
        allScreenings.remove(toDelete);
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        try {
            ScreeningDto toEdit = screeningsListView.getSelectionModel().getSelectedItem();
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_SCREENING);
            adminEditScreeningController.setScreeningDto(toEdit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_EDIT_SCREENING);
            adminEditScreeningController.resetScreeningDto();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleDateToday(ActionEvent event) {
        LocalDate targetDate = LocalDate.now();
        setScreeningsOn(targetDate);
    }

    @FXML
    private void handleDateTomorrow(ActionEvent event) {
        LocalDate targetDate = LocalDate.now().plusDays(1);
        setScreeningsOn(targetDate);
    }

    @FXML
    private void handleDate_plus_2(ActionEvent event) {
        LocalDate targetDate = LocalDate.now().plusDays(2);
        setScreeningsOn(targetDate);
    }

    @FXML
    private void handleDate_plus_3(ActionEvent event) {
        LocalDate targetDate = LocalDate.now().plusDays(3);
        setScreeningsOn(targetDate);
    }

    @FXML
    private void handleDate_plus_4(ActionEvent event) {
        LocalDate targetDate = LocalDate.now().plusDays(4);
        setScreeningsOn(targetDate);
    }

    @FXML
    private void handleBuy(ActionEvent event) {
        try {
            stageInitializer.loadStage(ControllerResource.ADMIN_BUY_TICKETS);
            adminBuyTicketsController.setScreeningDto(screeningsListView.getSelectionModel().getSelectedItem());
            adminBuyTicketsController.setMovieName(movieMap.get(screeningsListView.getSelectionModel().getSelectedItem().movieId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setScreeningsOn(LocalDate targetDate) {
        List<ScreeningDto> filteredScreenings = allScreenings.stream()
                .filter(screening -> screening.start().toLocalDate().equals(targetDate))
                .collect(Collectors.toList());

        screeningsDtoList.setAll(filteredScreenings);
    }
}
