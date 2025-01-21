package monaditto.cinemaproject.statistics;

import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.movie.Movie;
import monaditto.cinemaproject.purchase.Purchase;
import monaditto.cinemaproject.purchase.PurchaseRepository;
import monaditto.cinemaproject.purchase.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    private LocalDateTime getStartDateForPeriod(PeriodType periodType) {
        LocalDateTime now = LocalDateTime.now();
        switch (periodType) {
            case WEEK:
                return now.minusWeeks(1);
            case MONTH:
                return now.minusMonths(1);
            case YEAR:
                return now.minusYears(1);
            default:
                throw new IllegalArgumentException("Invalid period type");
        }
    }

    public double getRevenueForPeriod(PeriodType periodType) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (periodType) {
            case LAST_WEEK:
                startDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY).atStartOfDay();
                endDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
                break;
            case LAST_MONTH:
                startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
                endDate = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth()).atTime(23, 59, 59);
                break;
            case LAST_YEAR:
                startDate = LocalDate.of(LocalDate.now().getYear() - 1, 1, 1).atStartOfDay();
                endDate = LocalDate.of(LocalDate.now().getYear() - 1, 12, 31).atTime(23, 59, 59);
                break;
            case THIS_WEEK:
                startDate = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                endDate = startDate.plusDays(7);
                break;
            case THIS_MONTH:
                startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                endDate = startDate.plusMonths(1);
                break;
            case THIS_YEAR:
                startDate = LocalDate.now().withDayOfYear(1).atStartOfDay();
                endDate = startDate.plusYears(1);
                break;
            default:
                throw new IllegalArgumentException("Nieznany typ okresu: " + periodType);
        }

        Double revenue = purchaseRepository.calculateRevenueBetween(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }


    public MovieWithEarningsDto getMostPopularMovieForPeriod(PeriodType period) {
        LocalDateTime date = getStartDateForPeriod(period);
        List<Purchase> purchases = purchaseRepository.findAllByScreeningStartAfter(date);

        Map<Movie, Double> movieEarnings = purchases.stream()
                .filter(p -> p.getReservationStatus().equals(ReservationStatus.PAID))
                .collect(Collectors.groupingBy(
                        p -> p.getScreening().getMovie(),
                        Collectors.summingDouble(p -> p.getBoughtSeats() * p.getScreening().getPrice())
                ));

        return movieEarnings.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Movie movie = entry.getKey();
                    return new MovieWithEarningsDto(
                            movie.getId(),
                            movie.getTitle(),
                            entry.getValue()
                    );
                }).orElse(null);
    }

    public CategoryDto getMostPopularCategoryForPeriod(PeriodType period) {
        LocalDateTime date = getStartDateForPeriod(period);
        List<Purchase> purchases = purchaseRepository.findAllByScreeningStartAfter(date);

        Map<Category, Long> categoryPopularity = purchases.stream()
                .filter(p -> p.getReservationStatus().equals(ReservationStatus.PAID))
                .flatMap(p -> p.getScreening().getMovie().getCategories().stream()
                    .map(category -> new AbstractMap.SimpleEntry<>(category, p.getBoughtSeats())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingLong(Map.Entry::getValue)
                ));

        return categoryPopularity.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> new CategoryDto(entry.getKey().getCategoryId(), entry.getKey().getCategoryName()))
                .orElse(null);
    }

    public double getAverageAttendanceForPeriod(PeriodType periodType) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (periodType) {
            case LAST_WEEK:
                startDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY).atStartOfDay();
                endDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
                break;
            case LAST_MONTH:
                startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
                endDate = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth()).atTime(23, 59, 59);
                break;
            case LAST_YEAR:
                startDate = LocalDate.of(LocalDate.now().getYear() - 1, 1, 1).atStartOfDay();
                endDate = LocalDate.of(LocalDate.now().getYear() - 1, 12, 31).atTime(23, 59, 59);
                break;
            case THIS_WEEK:
                startDate = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                endDate = startDate.plusDays(7);
                break;
            case THIS_MONTH:
                startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                endDate = startDate.plusMonths(1);
                break;
            case THIS_YEAR:
                startDate = LocalDate.now().withDayOfYear(1).atStartOfDay();
                endDate = startDate.plusYears(1);
                break;
            default:
                throw new IllegalArgumentException("Nieznany typ okresu: " + periodType);
        }

        long totalSeats = purchaseRepository.calculateTotalSeatsForPeriod(startDate, endDate);
        long totalScreenings = purchaseRepository.calculateTotalScreeningsForPeriod(startDate, endDate);

        if (totalScreenings == 0) return 0.0;

        return (double) totalSeats / totalScreenings;
    }
}
