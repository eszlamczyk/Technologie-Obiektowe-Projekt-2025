package monaditto.cinemafront.databaseMapping;


import java.util.HashSet;
import java.util.Set;

public class Category {
    private Long id;

    private String categoryName;

    private Set<Movie> movies = new HashSet<>();

    public Category() {}

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }
}
