package monaditto.cinemaproject.RESTcontrollers;

import jakarta.annotation.security.RolesAllowed;
import monaditto.cinemaproject.opinion.OpinionDto;
import monaditto.cinemaproject.opinion.OpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/opinions")
public class OpinionController {

    @Autowired
    private OpinionService opinionService;

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @PostMapping
    public ResponseEntity<String> addOpinion(@RequestBody OpinionDto opinionDto) {
        try {
            opinionService.addOpinion(opinionDto);
            return ResponseEntity.ok("Successfully added opinion");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/{userId}/{movieId}")
    public ResponseEntity<OpinionDto> getOpinion(
            @PathVariable Long userId,
            @PathVariable Long movieId) {
        return ResponseEntity.ok(opinionService.getOpinion(userId, movieId));
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/{movieId}")
    public ResponseEntity<List<OpinionDto>> getOpinionsFor(@PathVariable Long movieId) {
        return ResponseEntity.ok(opinionService.getOpinionsFor(movieId));
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @PutMapping("/{userId}/{movieId}")
    public ResponseEntity<String> updateOpinion(
            @PathVariable Long userId,
            @PathVariable Long movieId,
            @RequestBody OpinionDto opinionDTO) {
        try {
            opinionService.updateOpinion(userId, movieId, opinionDTO);
            return ResponseEntity.ok("Opinion updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @DeleteMapping("/{userId}/{movieId}")
    public ResponseEntity<String> deleteOpinion(
            @PathVariable Long userId,
            @PathVariable Long movieId) {
        try {
            opinionService.deleteOpinion(userId, movieId);
            return ResponseEntity.ok("Opinion deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
