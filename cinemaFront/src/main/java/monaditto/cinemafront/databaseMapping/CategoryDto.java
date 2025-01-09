package monaditto.cinemafront.databaseMapping;


public record CategoryDto(
    Long id,
    String categoryName
) {
    public CategoryDto(String categoryName) {
        this(null, categoryName);
    }
}
