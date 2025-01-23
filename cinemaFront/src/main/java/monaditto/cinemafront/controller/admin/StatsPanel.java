package monaditto.cinemafront.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import monaditto.cinemafront.StageInitializer;
import monaditto.cinemafront.clientapi.StatsClientAPI;
import monaditto.cinemafront.controller.FXMLResourceEnum;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import monaditto.cinemafront.databaseMapping.MovieWithEarningsDto;
import monaditto.cinemafront.databaseMapping.PeriodType;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class StatsPanel {

    private final StageInitializer stageInitializer;

    private final StatsClientAPI statsClientAPI;

    @FXML
    private Label currentWeekRevenue;
    @FXML
    private Label lastWeekRevenue;
    @FXML
    private Label currentMonthRevenue;
    @FXML
    private Label lastMonthRevenue;
    @FXML
    private Label currentYearRevenue;
    @FXML
    private Label lastYearRevenue;

    @FXML
    private Label currentWeekTopMovie;
    @FXML
    private Label currentMonthTopMovie;
    @FXML
    private Label currentYearTopMovie;

    @FXML
    private Label currentWeekTopCategory;
    @FXML
    private Label currentMonthTopCategory;
    @FXML
    private Label currentYearTopCategory;

    @FXML
    private Label currentWeekAvgTraffic;
    @FXML
    private Label lastWeekAvgTraffic;
    @FXML
    private Label currentMonthAvgTraffic;
    @FXML
    private Label lastMonthAvgTraffic;
    @FXML
    private Label currentYearAvgTraffic;
    @FXML
    private Label lastYearAvgTraffic;

    public StatsPanel(StatsClientAPI statsClientAPI, StageInitializer stageInitializer) {
        this.statsClientAPI = statsClientAPI;
        this.stageInitializer = stageInitializer;
    }

    @FXML
    public void initialize() {
        loadRevenueStats();
        loadPopularMoviesStats();
        loadPopularCategoriesStats();
        loadAverageTrafficStats();
    }

    private void loadRevenueStats() {
        currentWeekRevenue.setText(formatCurrency(statsClientAPI.getRevenueForPeriod(PeriodType.THIS_WEEK)));
        lastWeekRevenue.setText(formatCurrency(statsClientAPI.getRevenueForPeriod(PeriodType.LAST_WEEK)));
        currentMonthRevenue.setText(formatCurrency(statsClientAPI.getRevenueForPeriod(PeriodType.THIS_MONTH)));
        lastMonthRevenue.setText(formatCurrency(statsClientAPI.getRevenueForPeriod(PeriodType.LAST_MONTH)));
        currentYearRevenue.setText(formatCurrency(statsClientAPI.getRevenueForPeriod(PeriodType.THIS_YEAR)));
        lastYearRevenue.setText(formatCurrency(statsClientAPI.getRevenueForPeriod(PeriodType.LAST_YEAR)));
    }

    private void loadPopularMoviesStats() {
        MovieWithEarningsDto weekMovie = statsClientAPI.getMostPopularMovieForPeriod(PeriodType.WEEK);
        MovieWithEarningsDto monthMovie = statsClientAPI.getMostPopularMovieForPeriod(PeriodType.MONTH);
        MovieWithEarningsDto yearMovie = statsClientAPI.getMostPopularMovieForPeriod(PeriodType.YEAR);

        currentWeekTopMovie.setText(formatMovie(weekMovie));
        currentMonthTopMovie.setText(formatMovie(monthMovie));
        currentYearTopMovie.setText(formatMovie(yearMovie));
    }

    private void loadPopularCategoriesStats() {
        CategoryDto weekCategory = statsClientAPI.getMostPopularCategoryForPeriod(PeriodType.WEEK);
        CategoryDto monthCategory = statsClientAPI.getMostPopularCategoryForPeriod(PeriodType.MONTH);
        CategoryDto yearCategory = statsClientAPI.getMostPopularCategoryForPeriod(PeriodType.YEAR);

        currentWeekTopCategory.setText(weekCategory.categoryName());
        currentMonthTopCategory.setText(monthCategory.categoryName());
        currentYearTopCategory.setText(yearCategory.categoryName());
    }

    private void loadAverageTrafficStats() {
        currentWeekAvgTraffic.setText(formatDouble(statsClientAPI.getAverageAttendanceForPeriod(PeriodType.THIS_WEEK)));
        lastWeekAvgTraffic.setText(formatDouble(statsClientAPI.getAverageAttendanceForPeriod(PeriodType.LAST_WEEK)));
        currentMonthAvgTraffic.setText(formatDouble(statsClientAPI.getAverageAttendanceForPeriod(PeriodType.THIS_MONTH)));
        lastMonthAvgTraffic.setText(formatDouble(statsClientAPI.getAverageAttendanceForPeriod(PeriodType.LAST_MONTH)));
        currentYearAvgTraffic.setText(formatDouble(statsClientAPI.getAverageAttendanceForPeriod(PeriodType.THIS_YEAR)));
        lastYearAvgTraffic.setText(formatDouble(statsClientAPI.getAverageAttendanceForPeriod(PeriodType.LAST_YEAR)));
    }

    private String formatCurrency(double value) {
        return String.format("%.2f PLN", value);
    }

    private String formatMovie(MovieWithEarningsDto movie) {
        return movie.title() + " (" + formatCurrency(movie.earnings()) + ")";
    }

    private String formatDouble(double value) {
        return String.format("%.2f", value);
    }

    public void handleGoBack(ActionEvent event) {
        try {
            stageInitializer.loadStage(FXMLResourceEnum.ADMIN_PANEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
