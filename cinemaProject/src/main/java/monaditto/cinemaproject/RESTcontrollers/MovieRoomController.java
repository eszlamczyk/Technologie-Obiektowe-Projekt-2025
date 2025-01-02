package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.movieRoom.MovieRoomDto;
import monaditto.cinemaproject.movieRoom.MovieRoomService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<MovieRoomDto> getAllMovieRooms() {
        return movieRoomService.getAllMovieRooms();
    }

    @GetMapping("/movieRoom/{movieRoomName}")
    public Optional<MovieRoomDto> getMovieRoom(@PathVariable String movieRoomName) {
        return movieRoomService.getMovieRoom(movieRoomName);
    }

    @PutMapping("/{movieRoomID}")
    public boolean updateMovieRoom(@PathVariable Long movieRoomID,
                                     @RequestBody MovieRoomDto movieRoomDto) {
        return movieRoomService.editMovieRoom(movieRoomID,movieRoomDto);
    }

    @PostMapping
    public boolean addMovieRoom(@RequestBody MovieRoomDto movieRoomDto) {
        return movieRoomService.createMovieRoom(movieRoomDto);
    }

    @DeleteMapping("delete/{movieRoomID}")
    public boolean deleteMovieRoom(@PathVariable Long movieRoomID) {
        return movieRoomService.deleteMovieRoom(movieRoomID);
    }

    @DeleteMapping
    public List<String> deleteMovieRooms(@RequestBody List<Long> movieRoomIDs) {
        return movieRoomService.deleteMovieRooms(movieRoomIDs);
    }

}
