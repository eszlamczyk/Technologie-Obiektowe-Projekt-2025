package monaditto.cinemafront.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public enum FXMLResourceEnum {
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
    ADMIN_BUY_TICKETS,
    ADMIN_PURCHASES,
    USER_PANEL,
    USER_RECOMMENDATIONS,
    USER_MOVIE,
    USER_SCREENINGS,
    USER_BUY_TICKETS,
    USER_PURCHASES,
    CASHIER_PANEL,
    CASHIER_MOVIE,
    CASHIER_SCREENINGS,
    CASHIER_PURCHASES,
    CASHIER_BUY_TICKETS,
    RATE_PANEL,
    USER_OPINIONS;

    public Resource getResource() {
        String resourceUrl = switch(this) {
            case LOGIN -> "fxml/authentication/Login.fxml";
            case REGISTRATION -> "fxml/authentication/Registration.fxml";
            case ADMIN_PANEL -> "fxml/admin/AdminPanel.fxml";
            case ADMIN_USER -> "fxml/admin/AdminUser.fxml";
            case ADMIN_EDIT_USER -> "fxml/edit/EditUser.fxml";
            case ADMIN_MOVIE -> "fxml/admin/AdminMovie.fxml";
            case ADMIN_EDIT_MOVIE -> "fxml/edit/EditMovie.fxml";
            case ADMIN_CATEGORY -> "fxml/admin/AdminCategory.fxml";
            case ADMIN_EDIT_CATEGORY -> "fxml/edit/EditCategory.fxml";
            case ADMIN_SCREENINGS -> "fxml/admin/AdminScreenings.fxml";
            case ADMIN_EDIT_SCREENING -> "fxml/edit/EditScreening.fxml";
            case ADMIN_MOVIE_ROOMS -> "fxml/admin/AdminMovieRoom.fxml";
            case ADMIN_BUY_TICKETS -> "fxml/admin/AdminBuyTickets.fxml";
            case ADMIN_EDIT_MOVIE_ROOM -> "fxml/edit/EditMovieRoom.fxml";
            case ADMIN_PURCHASES -> "fxml/admin/AdminPurchases.fxml";
            case USER_PANEL -> "fxml/user/UserPanel.fxml";
            case USER_RECOMMENDATIONS -> "fxml/user/UserRecommendation.fxml";
            case USER_MOVIE -> "fxml/user/UserMovie.fxml";
            case USER_SCREENINGS -> "fxml/user/UserScreenings.fxml";
            case USER_BUY_TICKETS -> "fxml/user/UserBuyTickets.fxml";
            case USER_PURCHASES -> "fxml/user/UserPurchases.fxml";
            case CASHIER_PANEL -> "fxml/cashier/CashierPanel.fxml";
            case CASHIER_MOVIE -> "fxml/cashier/CashierMovie.fxml";
            case CASHIER_SCREENINGS ->  "fxml/cashier/CashierScreenings.fxml";
            case CASHIER_PURCHASES -> "fxml/cashier/CashierPurchases.fxml";
            case CASHIER_BUY_TICKETS -> "fxml/cashier/CashierBuyTickets.fxml";
            case RATE_PANEL -> "fxml/user/UserRatePanel.fxml";
            case USER_OPINIONS -> "fxml/user/UserOpinions.fxml";
        };

        return new ClassPathResource(resourceUrl);
    }
}
