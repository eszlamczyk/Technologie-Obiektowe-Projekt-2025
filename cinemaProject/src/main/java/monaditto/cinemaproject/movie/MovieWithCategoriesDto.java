package monaditto.cinemaproject.movie;

import monaditto.cinemaproject.category.CategoryDto;

import java.util.List;

public record MovieWithCategoriesDto(
        MovieDto movieDto,
        List<CategoryDto> categories
) {}
