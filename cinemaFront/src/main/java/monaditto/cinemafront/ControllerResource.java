package monaditto.cinemafront;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public enum ControllerResource {
    LOGIN,
    REGISTRATION,
    ADMIN_PANEL,
    ADMIN_EDIT_USER;

    public Resource getResource() {
        String resourceUrl = switch(this) {
            case LOGIN -> "fxml/Login.fxml";
            case REGISTRATION -> "fxml/Registration.fxml";
            case ADMIN_PANEL -> "fxml/AdminPanel.fxml";
            case ADMIN_EDIT_USER -> "fxml/EditUser.fxml";
        };

        return new ClassPathResource(resourceUrl);
    }
}
