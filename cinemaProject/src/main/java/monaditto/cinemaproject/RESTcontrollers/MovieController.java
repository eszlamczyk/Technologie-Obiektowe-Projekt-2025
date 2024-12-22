package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.movie.CreateMovieStatus;
import monaditto.cinemaproject.movie.MovieDto;
import monaditto.cinemaproject.movie.MovieService;
import monaditto.cinemaproject.status.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping()
    public ResponseEntity<List<MovieDto>> getMovies() {
        return ResponseEntity.ok().body(movieService.getMovies());
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        if (movieService.deleteMovie(id)) {
            return ResponseEntity.ok().body("Successfully deleted the movie");
        }
        String message = String.format("Movie with given id (id = %d) doesn't exist", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @PutMapping("/create")
    public ResponseEntity<String> createMovie(@RequestBody MovieDto movieDto) {
        Status createMovieStatus = movieService.createMovie(movieDto);

        if (createMovieStatus.isSuccess()) {
            return ResponseEntity.ok(createMovieStatus.message());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createMovieStatus.message());
    }

    @PutMapping("/edit/set-categories/{id}")
    public ResponseEntity<String> setCategories(@PathVariable Long id, @RequestBody List<Long> categoryIds) {
        Status setCategoriesStatus = movieService.setCategories(id, categoryIds);

        if (setCategoriesStatus.isSuccess()) {
            return ResponseEntity.ok(setCategoriesStatus.message());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(setCategoriesStatus.message());
    }
}
