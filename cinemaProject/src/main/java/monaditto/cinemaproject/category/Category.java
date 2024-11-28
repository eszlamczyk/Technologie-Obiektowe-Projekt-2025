package monaditto.cinemaproject.category;

import jakarta.persistence.*;
import monaditto.cinemaproject.movie.Movie;

import java.util.Set;

@Entity
@Table(name = Category.TABLE_NAME)
public class Category {

    public static final String TABLE_NAME = "categories";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @ManyToMany(mappedBy = "categories")
    private Set<Movie> movies;

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
