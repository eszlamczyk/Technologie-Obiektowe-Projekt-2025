package monaditto.cinemafront.databaseMapping;

import java.util.HashSet;
import java.util.Set;

public class Movie {

    private Long id;

    private String title;

    private String description;

    private int duration;

    private Set<Screening> screenings = new HashSet<>();

    private Set<Category> categories;

    private Set<Opinion> opinions;

    public Movie() {}

    public Movie(String title, String description, int duration) {
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Set<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(Set<Screening> screenings) {
        this.screenings = screenings;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(Set<Opinion> opinions) {
        this.opinions = opinions;
    }

    public void addScreening(Screening screening) {
        screenings.add(screening);
        screening.setMovie(this);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void addOpinion(Opinion opinion) {
        opinions.add(opinion);
    }
}
