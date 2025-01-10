package monaditto.cinemaproject.movie;

import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.category.CategoryRepository;
import monaditto.cinemaproject.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public List<MovieDto> getComingSoonMovies() {
        return movieRepository.findComingSoonMovies(LocalDate.now()).stream()
                .map(MovieDto::movieToMovieDto)
                .limit(5)
                .toList();
    }

    public Optional<MovieDto> getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(MovieDto::movieToMovieDto);
    }

    public List<CategoryDto> getMovieCategories(Long id) {
        return movieRepository.findById(id)
                .map(Movie::getCategories)
                .map(categories -> categories.stream()
                        .map(CategoryDto::categoryToCategoryDto)
                        .toList()
                )
                .orElseGet(List::of);
    }

    public CreateMovieStatus createMovie(MovieDto movieDto) {
        return createMovie(movieDto, List.of());
    }

    public CreateMovieStatus createMovie(MovieDto movieDto, List<CategoryDto> categories) {
        CreateMovieStatus movieStatus = movieValidator.validateMovieDto(movieDto);
        if (movieStatus != CreateMovieStatus.SUCCESS) {
            return movieStatus;
        }

        Movie movie = createMovieFromMovieDto(movieDto);
        movieRepository.save(movie);

        List<Long> categoryIds = getCategoriesIds(categories);

        return setCategories(movie.getId(), categoryIds);
    }

    public CreateMovieStatus createMovieByNames(MovieDto movieDto, List<String> categoryNames) {
        CreateMovieStatus movieStatus = movieValidator.validateMovieDto(movieDto);
        if (movieStatus != CreateMovieStatus.SUCCESS) {
            return movieStatus;
        }

        Movie movie = createMovieFromMovieDto(movieDto);
        movieRepository.save(movie);

        List<Long> categoryIds = categoryService.getCategoryIdsByName(categoryNames);

        return setCategories(movie.getId(), categoryIds);
    }

    public CreateMovieStatus editMovie(Long movieId, MovieDto newMovieDto, List<CategoryDto> categories) {
        CreateMovieStatus movieStatus = movieValidator.validateMovieDto(newMovieDto);
        if (movieStatus != CreateMovieStatus.SUCCESS) {
            return movieStatus;
        }

        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        if (optionalMovie.isEmpty()) {
            return CreateMovieStatus.MOVIE_DOESNT_EXIST;
        }
        Movie movie = optionalMovie.get();

        updateMovie(movie, newMovieDto);

        List<Long> categoriesIds = getCategoriesIds(categories);
        CreateMovieStatus setCategoriesStatus = setCategories(movie.getId(), categoriesIds);
        if (!setCategoriesStatus.isSuccess()) {
            return setCategoriesStatus;
        }

        movieRepository.save(movie);
        return CreateMovieStatus.SUCCESS;
    }

    public boolean deleteMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            return false;
        }

        movieRepository.delete(movie.get());
        return true;
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

    private void updateMovie(Movie movie, MovieDto movieDto) {
        movie.setTitle(movieDto.title());
        movie.setDescription(movieDto.description());
        movie.setDuration(movieDto.duration());
        movie.setPosterUrl(movieDto.posterUrl());
        movie.setReleaseDate(movieDto.releaseDate());
    }

    private List<Long> getCategoriesIds(List<CategoryDto> categories) {
        return categories.stream()
                .map(CategoryDto::id)
                .toList();
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
