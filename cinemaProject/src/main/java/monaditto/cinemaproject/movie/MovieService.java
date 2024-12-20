package monaditto.cinemaproject.movie;

import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    private final CategoryRepository categoryRepository;

    private final MovieValidator movieValidator;

    @Autowired
    public MovieService(MovieRepository movieRepository, CategoryRepository categoryRepository, MovieValidator movieValidator) {
        this.movieRepository = movieRepository;
        this.categoryRepository = categoryRepository;
        this.movieValidator = movieValidator;
    }

    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public CreateMovieStatus createMovie(MovieDto movieDto) {
        CreateMovieStatus movieStatus = movieValidator.validateMovieDto(movieDto);
        if (movieStatus != CreateMovieStatus.SUCCESS) {
            return movieStatus;
        }
        Movie movie = createMovieFromMovieDto(movieDto);

        movieRepository.save(movie);

        return CreateMovieStatus.SUCCESS;
    }

    public CreateMovieStatus addCategories(Long movieId, List<Long> categoryIds) {
        Movie movie = movieRepository.findById(movieId)
                .orElse(null);
        if (movie == null) {
            return CreateMovieStatus.MOVIE_DOESNT_EXIST;
        }

        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElse(null);
            if (category == null) {
                return CreateMovieStatus.CATEGORY_DOESNT_EXIST;
            }
            categories.add(category);
        }

        categories.forEach(category -> {
            movie.addCategory(category);
            category.addMovie(movie);
            categoryRepository.save(category);
        });
        movieRepository.save(movie);

        return CreateMovieStatus.SUCCESS;
    }

    private Movie createMovieFromMovieDto(MovieDto movieDto) {
        return new Movie(
                movieDto.title(),
                movieDto.description(),
                movieDto.duration(),
                movieDto.posterUrl()
        );
    }
}
