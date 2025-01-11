package monaditto.cinemaproject.RESTcontrollers;

import jakarta.annotation.security.RolesAllowed;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.movie.CreateMovieStatus;
import monaditto.cinemaproject.movie.MovieDto;
import monaditto.cinemaproject.movie.MovieService;
import monaditto.cinemaproject.movie.MovieWithCategoriesDto;
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

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping()
    public ResponseEntity<List<MovieDto>> getMovies() {
        return ResponseEntity.ok().body(movieService.getMovies());
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/coming-soon")
    public ResponseEntity<List<MovieDto>> getComingSoonMovies() {
        return ResponseEntity.ok().body(movieService.getComingSoonMovies());
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @PutMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMovies(@RequestBody String query) {
        return ResponseEntity.ok().body(movieService.searchMovies(query));
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        if (movieService.deleteMovie(id)) {
            return ResponseEntity.ok().body("Successfully deleted the movie");
        }
        String message = String.format("Movie with given id (id = %d) doesn't exist", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/create")
    public ResponseEntity<String> createMovie(@RequestBody MovieWithCategoriesDto wrapperDto) {
        Status createMovieStatus = movieService.createMovie(wrapperDto.movieDto(), wrapperDto.categories());

        if (createMovieStatus.isSuccess()) {
            return ResponseEntity.ok(createMovieStatus.message());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createMovieStatus.message());
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editMovie(@PathVariable Long id, @RequestBody MovieWithCategoriesDto wrapperDto) {
        Status editMovieStatus = movieService.editMovie(id, wrapperDto.movieDto(), wrapperDto.categories());

        if (editMovieStatus.isSuccess()) {
            return ResponseEntity.ok(editMovieStatus.message());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(editMovieStatus.message());
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/categories/{id}")
    public ResponseEntity<List<CategoryDto>> getMovieCategories(@PathVariable Long id) {
        List<CategoryDto> categories = movieService.getMovieCategories(id);

        return ResponseEntity.ok().body(categories);
    }
}
