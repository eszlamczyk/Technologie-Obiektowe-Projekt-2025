package monaditto.cinemaproject.category;

import jakarta.persistence.Column;
import monaditto.cinemaproject.movie.Movie;
import monaditto.cinemaproject.movie.MovieDto;

public record CategoryDto (
    Long id,
    String categoryName
) {
    public CategoryDto(String categoryName) {
        this(null, categoryName);
    }

    public static CategoryDto categoryToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryDto(
                category.getCategoryId(),
                category.getCategoryName()
        );
    }
}
