package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.screening.Screening;
import monaditto.cinemaproject.screening.ScreeningDto;
import monaditto.cinemaproject.screening.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {

    @Autowired
    private ScreeningService screeningService;

    @GetMapping
    public ResponseEntity<List<Screening>> getAllScreenings() {
        List<Screening> screenings = screeningService.getAllScreenings();
        return ResponseEntity.ok(screenings);
    }

    @PostMapping
    public ResponseEntity<Screening> createScreening(@RequestBody ScreeningDto screeningDto) {
        Screening createdScreening = screeningService.saveScreening(screeningDto);
        return ResponseEntity.ok(createdScreening);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Screening> getScreeningById(@PathVariable Long id) {
        Optional<Screening> screening = screeningService.getScreeningById(id);
        return screening.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Screening> updateScreening(@PathVariable Long id, @RequestBody ScreeningDto screeningDto) {
        return ResponseEntity.ok(screeningService.updateScreening(id, screeningDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreening(@PathVariable Long id) {
        if (screeningService.deleteScreening(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<Screening>> getScreeningsByDate(@RequestParam LocalDate date) {
        List<Screening> screenings = screeningService.getScreeningsByDate(date);
        return ResponseEntity.ok(screenings);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Screening>> getUpcomingScreenings(@RequestParam LocalDateTime dateTime) {
        List<Screening> screenings = screeningService.getUpcomingScreeningsAfter(dateTime);
        return ResponseEntity.ok(screenings);
    }
}
