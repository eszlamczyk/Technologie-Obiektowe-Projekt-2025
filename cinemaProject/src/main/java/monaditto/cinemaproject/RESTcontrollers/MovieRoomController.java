package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.movieRoom.MovieRoomDto;
import monaditto.cinemaproject.movieRoom.MovieRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movieRooms")
public class MovieRoomController {

    private final MovieRoomService movieRoomService;

    @Autowired
    public MovieRoomController(MovieRoomService movieRoomService) {
        this.movieRoomService = movieRoomService;
    }

    @GetMapping
    public ResponseEntity<List<MovieRoomDto>> getAllMovieRooms() {
        return ResponseEntity.ok().body(movieRoomService.getAllMovieRooms());
    }

    @GetMapping("/movieRoom/{movieRoomName}")
    public ResponseEntity<MovieRoomDto> getMovieRoom(@PathVariable String movieRoomName) {
        Optional<MovieRoomDto> optionalMovieRoomDto = movieRoomService.getMovieRoom(movieRoomName);
        return optionalMovieRoomDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{movieRoomID}")
    public ResponseEntity<String> updateMovieRoom(@PathVariable Long movieRoomID,
                                     @RequestBody MovieRoomDto movieRoomDto) {
        return switch(movieRoomService.editMovieRoom(movieRoomID,movieRoomDto)){
            case SUCCESS -> ResponseEntity.ok("Room updated successfully");
            case INCORRECT_ID -> ResponseEntity.status(HttpStatus.CONFLICT).body("Room name already taken");
            case MOVIE_ROOM_NAME_TAKEN -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room ID not found");
        };
    }

    @PostMapping
    public ResponseEntity<String> createMovieRoom(@RequestBody MovieRoomDto movieRoomDto) {
        return switch(movieRoomService.createMovieRoom(movieRoomDto)){
            case SUCCESS -> ResponseEntity.ok("Room updated successfully");
            case INCORRECT_ID -> ResponseEntity.status(HttpStatus.CONFLICT).body("Room name already taken");
            case MOVIE_ROOM_NAME_TAKEN -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room ID not found");
        };
    }

    @DeleteMapping("/{movieRoomID}")
    public ResponseEntity<String> deleteMovieRoom(@PathVariable Long movieRoomID) {
        if(movieRoomService.deleteMovieRoom(movieRoomID)){
            return ResponseEntity.ok("Successfully deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category ID not found");
    }
}
