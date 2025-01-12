package monaditto.cinemafront.controller;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.MovieWithAverageRatingDto;
import monaditto.cinemafront.request.PosterDownloader;
import org.springframework.stereotype.Component;

@Component
public class MovieCellCreator {

    private PosterDownloader posterDownloader;

    public MovieCellCreator(PosterDownloader posterDownloader) {
        this.posterDownloader = posterDownloader;
    }

    public HBox createMovieCell(MovieDto movieDto) {
        HBox hBox = new HBox(10);
        hBox.setStyle("-fx-padding: 10;");

        ImageView imageView = getImageView(movieDto);

        VBox vBox = getDescription(movieDto);

        hBox.getChildren().addAll(imageView, vBox);
        return hBox;
    }


    public HBox createMovieCell(MovieWithAverageRatingDto movieWithAverageRatingDto) {
        HBox hBox = new HBox(10);
        hBox.setStyle("-fx-padding: 10;");

        ImageView imageView = getImageView(movieWithAverageRatingDto.movieDto());

        VBox vBox = getDescription(movieWithAverageRatingDto.movieDto());

        Label label = new Label("Average rating = " + movieWithAverageRatingDto.averageRating().toString());

        hBox.getChildren().addAll(imageView, vBox, label);
        return hBox;
    }

    private VBox getDescription(MovieDto movieDto) {
        VBox vBox = new VBox();
        Label titleLabel = new Label(movieDto.title());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label releaseDateLabel = new Label("Premiere date: "+ movieDto.releaseDate().toString());
        releaseDateLabel.setStyle("-fx-font-size: 14px;");

        vBox.getChildren().addAll(titleLabel, releaseDateLabel);
        return vBox;
    }

    private ImageView getImageView(MovieDto movieDto) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(75);
        posterDownloader.isPosterUrlValid(movieDto.posterUrl());
        Image image = posterDownloader.getPoster();
        imageView.setImage(image);
        return imageView;
    }
}
