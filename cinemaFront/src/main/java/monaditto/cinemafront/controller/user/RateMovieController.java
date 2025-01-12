package monaditto.cinemafront.controller.user;

import javafx.application.Platform;
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
import monaditto.cinemafront.response.ResponseResult;
import monaditto.cinemafront.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.http.HttpResponse;

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

    private OpinionDto opinion;

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
        if (opinion == null) {
            createOpinion();
        } else {
            editOpinion();
        }
    }

    private void editOpinion() {
        OpinionDto opinionDto = createOpinionDto();
        opinionClientAPI.editOpinion(opinionDto)
                .thenAccept(response -> handleResponse(response, "Opinion Edited successfully!", "Failed to Edit opinion"))
                .exceptionally(this::handleException);
    }

    private void createOpinion() {
        OpinionDto opinionDto = createOpinionDto();
        opinionClientAPI.addOpinion(opinionDto)
                .thenAccept(response -> handleResponse(response, "Opinion submitted successfully!", "Failed to submit opinion"))
                .exceptionally(this::handleException);
    }

    private OpinionDto createOpinionDto() {
        double rating = ratingSlider.getValue();
        String comment = commentTextArea.getText();

        return new OpinionDto(
                sessionContext.getUserId(),
                movie.id(),
                rating,
                comment
        );
    }

    private void handleResponse(ResponseResult response, String successMessage, String failureMessage) {
        Platform.runLater(() -> {
            if (response.statusCode() == 200) {
                statusLabel.setText(successMessage);
                statusLabel.setStyle("-fx-text-fill: green;");
                submitButton.setDisable(true);
            } else {
                statusLabel.setText(failureMessage + ": " + response.body());
                statusLabel.setStyle("-fx-text-fill: red;");
                submitButton.setDisable(false);
            }
            statusLabel.setVisible(true);
        });
    }

    private Void handleException(Throwable ex) {
        Platform.runLater(() -> {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setVisible(true);
            submitButton.setDisable(false);
        });
        return null;
    }



    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            if (opinion == null) {
                stageInitializer.loadStage(FXMLResourceEnum.USER_MOVIE);
            } else {
                opinion = null;
                stageInitializer.loadStage(FXMLResourceEnum.USER_OPINIONS);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOpinionDto(OpinionDto opinionDto, MovieDto movie) {
        setMovie(movie);
        this.opinion = opinionDto;
        loadEditOpinion();
    }

    private void loadEditOpinion() {
        ratingSlider.setValue(opinion.rating());
        commentTextArea.setText(opinion.comment());
    }
}
