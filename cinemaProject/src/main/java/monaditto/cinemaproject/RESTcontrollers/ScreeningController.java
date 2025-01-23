package monaditto.cinemaproject.RESTcontrollers;

import jakarta.annotation.security.RolesAllowed;
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

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping
    public ResponseEntity<List<ScreeningDto>> getAllScreenings() {
        List<ScreeningDto> screenings = screeningService.getAllScreenings();
        return ResponseEntity.ok(screenings);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping
    public ResponseEntity<ScreeningDto> createScreening(@RequestBody ScreeningDto screeningDto) {
        ScreeningDto createdScreening = screeningService.saveScreening(screeningDto);
        return ResponseEntity.ok(createdScreening);
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/{id}")
    public ResponseEntity<ScreeningDto> getScreeningById(@PathVariable Long id) {
        Optional<ScreeningDto> screening = screeningService.getScreeningById(id);
        return screening.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<ScreeningDto> updateScreening(@PathVariable Long id, @RequestBody ScreeningDto screeningDto) {
        return ResponseEntity.ok(screeningService.updateScreening(id, screeningDto));
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteScreening(@PathVariable Long id) {
        if (screeningService.deleteScreening(id)) {
            return ResponseEntity.ok().body("Successfully deleted the screening");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/by-date")
    public ResponseEntity<List<ScreeningDto>> getScreeningsByDate(@RequestParam LocalDate date) {
        List<ScreeningDto> screenings = screeningService.getScreeningsByDate(date);
        return ResponseEntity.ok(screenings);
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/upcoming")
    public ResponseEntity<List<ScreeningDto>> getUpcomingScreenings(@RequestParam LocalDateTime dateTime) {
        List<ScreeningDto> screenings = screeningService.getUpcomingScreeningsAfter(dateTime);
        return ResponseEntity.ok(screenings);
    }
}
