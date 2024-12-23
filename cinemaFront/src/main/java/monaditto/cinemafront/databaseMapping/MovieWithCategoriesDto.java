package monaditto.cinemafront.databaseMapping;

import java.util.List;

public record MovieWithCategoriesDto(
        MovieDto movieDto,
        List<CategoryDto> categories
) {}
