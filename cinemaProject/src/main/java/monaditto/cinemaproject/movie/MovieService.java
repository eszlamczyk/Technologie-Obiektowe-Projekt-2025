package monaditto.cinemaproject.movie;

import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.category.CategoryRepository;
import monaditto.cinemaproject.category.CategoryService;
import monaditto.cinemaproject.opinion.OpinionRepository;
import monaditto.cinemaproject.purchase.PurchaseService;
import monaditto.cinemaproject.search.Trie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    private final CategoryRepository categoryRepository;

    private final OpinionRepository opinionRepository;

    private final CategoryService categoryService;

    private final PurchaseService purchaseService;

    private final MovieValidator movieValidator;

    private final Trie trie;

    @Autowired
    public MovieService(MovieRepository movieRepository,
                        CategoryRepository categoryRepository,
                        OpinionRepository opinionRepository,
                        MovieValidator movieValidator,
                        CategoryService categoryService,
                        PurchaseService purchaseService,
                        Trie trie) {
        this.movieRepository = movieRepository;
        this.categoryRepository = categoryRepository;
        this.opinionRepository = opinionRepository;
        this.movieValidator = movieValidator;
        this.categoryService = categoryService;
        this.purchaseService = purchaseService;
        this.trie = trie;

        buildTrie();
    }

    private void buildTrie() {
        movieRepository.findAll().stream()
                .map(MovieDto::movieToMovieDto)
                .forEach(trie::insert);
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

    public List<MovieWithAverageRatingDto> getTopRatedMovies() {
        List<Object[]> topRatedMovies = movieRepository.findTopRatedMovies();
        return topRatedMovies.stream()
                .limit(7)
                .map(result -> {
                    Movie movie = (Movie) result[0];
                    Double avgRating = (Double) result[1];
                    return new MovieWithAverageRatingDto(MovieDto.movieToMovieDto(movie), avgRating);
                })
                .toList();
    }

    public List<MovieWithAverageRatingDto> getRecommendedMovies(Long userId) {
        int maxSize = 7;

        Long categoryId = purchaseService.getMostPurchasedCategoryIdForUser(userId);
        if (categoryId == 0) {
            return new ArrayList<>();
        }

        List<Movie> recommendedNotWatchedMovies =
                movieRepository.findMoviesByCategoryAndNotWatchedByUser(categoryId, userId, LocalDateTime.now());

        if (recommendedNotWatchedMovies.size() < maxSize) {
            List<Movie> randomMovies = movieRepository.findOtherMoviesNotWatchedByUser(categoryId, userId, LocalDateTime.now());
            Collections.shuffle(randomMovies);
            randomMovies = randomMovies.subList(0, maxSize - recommendedNotWatchedMovies.size());
            recommendedNotWatchedMovies.addAll(randomMovies);
        }

        List<MovieWithAverageRatingDto> recommendations = computeAverageRatings(recommendedNotWatchedMovies);
        recommendations.sort(MovieService::sortRecommendedMovies);

        return recommendations.stream().limit(maxSize).collect(Collectors.toList());
    }

    private static int sortRecommendedMovies(MovieWithAverageRatingDto movie1, MovieWithAverageRatingDto movie2) {
        Double rating1 = movie1.averageRating();
        Double rating2 = movie2.averageRating();

        if (rating1 == null) {
            rating1 = 20.0;
        }
        if (rating2 == null) {
            rating2 = 20.0;
        }

        return Double.compare(rating2, rating1);
    }

    private List<MovieWithAverageRatingDto> computeAverageRatings(List<Movie> movies) {
        List<MovieWithAverageRatingDto> moviesWithAverageRating = new ArrayList<>();
        for (Movie movie : movies) {
            Double averageRating = opinionRepository.findAverageRatingByMovieId(movie.getId());
            MovieWithAverageRatingDto recommendation =
                    new MovieWithAverageRatingDto(MovieDto.movieToMovieDto(movie), averageRating);
            moviesWithAverageRating.add(recommendation);
        }

        return moviesWithAverageRating;
    }

    public List<MovieDto> searchMovies(String query) {
        return trie.search(query);
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


    public CreateMovieStatus createMovie(MovieWithCategoriesDto movieWithCategoriesDto) {
        return createMovie(movieWithCategoriesDto.movieDto(), movieWithCategoriesDto.categories());
    }

    public CreateMovieStatus createMovie(MovieDto movieDto, List<CategoryDto> categories) {
        CreateMovieStatus movieStatus = movieValidator.validateMovieDto(movieDto);
        if (movieStatus != CreateMovieStatus.SUCCESS) {
            return movieStatus;
        }

        Movie movie = createMovieFromMovieDto(movieDto);
        movieRepository.save(movie);

        List<Long> categoryIds = getCategoriesIds(categories);

        CreateMovieStatus createMovieStatus = setCategories(movie.getId(), categoryIds);
        if (createMovieStatus.isSuccess()) {
            trie.insert(MovieDto.movieToMovieDto(movie));
        }
        return createMovieStatus;
    }

    public CreateMovieStatus createMovieByNames(MovieDto movieDto, List<String> categoryNames) {
        CreateMovieStatus movieStatus = movieValidator.validateMovieDto(movieDto);
        if (movieStatus != CreateMovieStatus.SUCCESS) {
            return movieStatus;
        }

        Movie movie = createMovieFromMovieDto(movieDto);
        movieRepository.save(movie);

        List<Long> categoryIds = categoryService.getCategoryIdsByName(categoryNames);

        CreateMovieStatus createMovieStatus = setCategories(movie.getId(), categoryIds);
        if (createMovieStatus.isSuccess()) {
            trie.insert(MovieDto.movieToMovieDto(movie));
        }
        return createMovieStatus;
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
        MovieDto oldMovieDto = MovieDto.movieToMovieDto(movie);

        updateMovie(movie, newMovieDto);

        List<Long> categoriesIds = getCategoriesIds(categories);
        CreateMovieStatus setCategoriesStatus = setCategories(movie.getId(), categoriesIds);
        if (!setCategoriesStatus.isSuccess()) {
            return setCategoriesStatus;
        }

        movieRepository.save(movie);
        trie.remove(oldMovieDto);
        trie.insert(MovieDto.movieToMovieDto(movie));

        return CreateMovieStatus.SUCCESS;
    }

    public boolean deleteMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            return false;
        }

        movieRepository.delete(movie.get());
        trie.remove(MovieDto.movieToMovieDto(movie.get()));
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
