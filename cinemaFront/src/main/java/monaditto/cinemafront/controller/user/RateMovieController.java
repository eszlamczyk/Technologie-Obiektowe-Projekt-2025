package monaditto.cinemafront.controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.OpinionClientAPI;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.clientapi.MovieClientAPI;
import monaditto.cinemafront.databaseMapping.OpinionDto;
import monaditto.cinemafront.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class RateMovieController {

    @FXML
    private Label movieTitleLabel;

    @FXML
    private ImageView moviePosterImageView;

    @FXML
    private Button submitButton;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Slider ratingSlider;

    @FXML
    private Label statusLabel;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Rectangle backgroundRectangle;

    private final SessionContext sessionContext;

    private MovieDto movie;

    private final StageInitializer stageInitializer;

    private final OpinionClientAPI opinionClientAPI;

    @Autowired
    public RateMovieController(OpinionClientAPI opinionClientAPI,
                               StageInitializer stageInitializer,
                               SessionContext sessionContext) {
        this.opinionClientAPI = opinionClientAPI;
        this.stageInitializer = stageInitializer;
        this.sessionContext = sessionContext;
    }

    @FXML
    private void initialize() {
        initializeResponsiveness();
    }

    private void initializeResponsiveness() {
        backgroundRectangle.widthProperty().bind(rootPane.widthProperty());
        backgroundRectangle.heightProperty().bind(rootPane.heightProperty());
    }

    public void setMovie(MovieDto movie) {
        this.movie = movie;
        movieTitleLabel.setText(movie.title());
        moviePosterImageView.setImage(new Image(movie.posterUrl()));
    }

    @FXML
    private void handleSubmitRating(ActionEvent event) {
        double rating = ratingSlider.getValue();
        String comment = commentTextArea.getText();

        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println(sessionContext.getUserId());

        OpinionDto opinionDto = new OpinionDto(
                sessionContext.getUserId(),
                movie.id(),
                rating,
                comment
        );

        opinionClientAPI.addOpinion(opinionDto)
                .thenAccept(response -> {
                    statusLabel.setText("Opinion submitted successfully!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setVisible(true);
                })
                .exceptionally(ex -> {
                    statusLabel.setText("Failed to submit opinion: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setVisible(true);
                    return null;
                });

        submitButton.setDisable(true);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.USER_MOVIE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) movieTitleLabel.getScene().getWindow();
        stage.close();
    }
}
