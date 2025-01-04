package monaditto.cinemafront.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public enum ControllerResource {
    LOGIN,
    REGISTRATION,
    ADMIN_PANEL,
    ADMIN_USER,
    ADMIN_EDIT_USER,
    ADMIN_MOVIE,
    ADMIN_EDIT_MOVIE,
    ADMIN_CATEGORY,
    ADMIN_EDIT_CATEGORY,
    ADMIN_SCREENINGS,
    ADMIN_EDIT_SCREENING,
    ADMIN_MOVIE_ROOMS,
    ADMIN_EDIT_MOVIE_ROOM,
    USER_PANEL,
    USER_MOVIE,
    USER_SCREENINGS;

    public Resource getResource() {
        String resourceUrl = switch(this) {
            case LOGIN -> "fxml/Login.fxml";
            case REGISTRATION -> "fxml/Registration.fxml";
            case ADMIN_PANEL -> "fxml/AdminPanel.fxml";
            case ADMIN_USER -> "fxml/AdminUser.fxml";
            case ADMIN_EDIT_USER -> "fxml/EditUser.fxml";
            case ADMIN_MOVIE -> "fxml/AdminMovie.fxml";
            case ADMIN_EDIT_MOVIE -> "fxml/EditMovie.fxml";
            case ADMIN_CATEGORY -> "fxml/AdminCategory.fxml";
            case ADMIN_EDIT_CATEGORY -> "fxml/EditCategory.fxml";
            case ADMIN_SCREENINGS -> "fxml/AdminScreenings.fxml";
            case ADMIN_EDIT_SCREENING -> "fxml/EditScreening.fxml";
            case ADMIN_MOVIE_ROOMS -> "fxml/AdminMovieRoom.fxml";
            case ADMIN_EDIT_MOVIE_ROOM -> "fxml/EditMovieRoom.fxml";
            case USER_PANEL -> "fxml/UserPanel.fxml";
            case USER_MOVIE -> "fxml/UserMovie.fxml";
            case USER_SCREENINGS -> "fxml/UserScreenings.fxml";
        };

        return new ClassPathResource(resourceUrl);
    }
}
