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
    @GetMapping("/most-popular-movie/{periodType}")
    public MovieWithEarningsDto getMostPopularMovieLastWeek(@PathVariable PeriodType periodType) {
        return statisticsService.getMostPopularMovieForPeriod(periodType);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/most-popular-category/{periodType}")
    public CategoryDto getMostPopularCategoryLastWeek(@PathVariable PeriodType periodType) {
        return statisticsService.getMostPopularCategoryForPeriod(periodType);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/average-attendance/{periodType}")
    public ResponseEntity<Double> getAverageAttendanceForPeriod(@PathVariable PeriodType periodType) {
        double averageAttendance = statisticsService.getAverageAttendanceForPeriod(periodType);
        return ResponseEntity.ok(averageAttendance);
    }
}
