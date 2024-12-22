package monaditto.cinemaproject.movie;

import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.category.CategoryRepository;
import monaditto.cinemaproject.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    private final CategoryRepository categoryRepository;

    private final CategoryService categoryService;

    private final MovieValidator movieValidator;

    @Autowired
    public MovieService(MovieRepository movieRepository,
                        CategoryRepository categoryRepository,
                        MovieValidator movieValidator,
                        CategoryService categoryService) {
        this.movieRepository = movieRepository;
        this.categoryRepository = categoryRepository;
        this.movieValidator = movieValidator;
        this.categoryService = categoryService;
    }

    public List<MovieDto> getMovies() {
        return movieRepository.findAll().stream()
                .map(MovieDto::movieToMovieDto)
                .toList();
    }

    public Optional<MovieDto> getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(MovieDto::movieToMovieDto);
    }

    public CreateMovieStatus createMovie(MovieDto movieDto) {
        return createMovie(movieDto, List.of());
    }

    public CreateMovieStatus createMovie(MovieDto movieDto, List<String> categoryNames) {
        CreateMovieStatus movieStatus = movieValidator.validateMovieDto(movieDto);
        if (movieStatus != CreateMovieStatus.SUCCESS) {
            return movieStatus;
        }

        Movie movie = createMovieFromMovieDto(movieDto);
        movieRepository.save(movie);

        List<Long> categoryIds = categoryService.getCategoryIdsByName(categoryNames);

        return setCategories(movie.getId(), categoryIds);
    }

    public CreateMovieStatus setCategories(Long movieId, List<Long> categoryIds) {
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

        movie.clearCategories();
        categories.forEach(movie::addCategory);
        movieRepository.save(movie);

        return CreateMovieStatus.SUCCESS;
    }

    private Movie createMovieFromMovieDto(MovieDto movieDto) {
        return new Movie(
                movieDto.title(),
                movieDto.description(),
                movieDto.duration(),
                movieDto.posterUrl(),
                movieDto.releaseDate()
        );
    }
}
