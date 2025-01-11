package monaditto.cinemaproject.RESTcontrollers;

import jakarta.annotation.security.RolesAllowed;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.statistics.MovieWithEarningsDto;
import monaditto.cinemaproject.statistics.PeriodType;
import monaditto.cinemaproject.statistics.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @RolesAllowed({"ADMIN"})
    @GetMapping("/revenue/{periodType}")
    public ResponseEntity<Double> getRevenueForPeriod(@PathVariable PeriodType periodType) {
        double revenue = statisticsService.getRevenueForPeriod(periodType);
        return ResponseEntity.ok(revenue);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/most-popular-movie-week")
    public MovieWithEarningsDto getMostPopularMovieLastWeek() {
        return statisticsService.getMostPopularMovieForPeriod(PeriodType.WEEK);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/most-popular-movie-month")
    public MovieWithEarningsDto getMostPopularMovieLastMonth() {
        return statisticsService.getMostPopularMovieForPeriod(PeriodType.MONTH);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/most-popular-movie-year")
    public MovieWithEarningsDto getMostPopularMovieLastYear() {
        return statisticsService.getMostPopularMovieForPeriod(PeriodType.YEAR);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/most-popular-category-week")
    public CategoryDto getMostPopularCategoryLastWeek() {
        return statisticsService.getMostPopularCategoryForPeriod(PeriodType.WEEK);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/most-popular-category-month")
    public CategoryDto getMostPopularCategoryLastMonth() {
        return statisticsService.getMostPopularCategoryForPeriod(PeriodType.MONTH);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/most-popular-category-year")
    public CategoryDto getMostPopularCategoryLastYear() {
        return statisticsService.getMostPopularCategoryForPeriod(PeriodType.YEAR);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/average-attendance/{periodType}")
    public ResponseEntity<Double> getAverageAttendanceForPeriod(@PathVariable PeriodType periodType) {
        double averageAttendance = statisticsService.getAverageAttendanceForPeriod(periodType);
        return ResponseEntity.ok(averageAttendance);
    }
}
