package monaditto.cinemafront.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public enum ControllerResource {
    LOGIN,
    REGISTRATION,
    ADMIN_PANEL,
    ADMIN_USER,
    ADMIN_EDIT_USER,
    ADMIN_MOVIE;

    public Resource getResource() {
        String resourceUrl = switch(this) {
            case LOGIN -> "fxml/Login.fxml";
            case REGISTRATION -> "fxml/Registration.fxml";
            case ADMIN_PANEL -> "fxml/AdminPanel.fxml";
            case ADMIN_USER -> "fxml/AdminUser.fxml";
            case ADMIN_EDIT_USER -> "fxml/EditUser.fxml";
            case ADMIN_MOVIE -> "fxml/AdminMovie.fxml";
        };

        return new ClassPathResource(resourceUrl);
    }
}
