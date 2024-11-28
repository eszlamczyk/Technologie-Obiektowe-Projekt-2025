package monaditto.cinemaproject.movie;

import jakarta.persistence.*;
import monaditto.cinemaproject.category.Category;
import monaditto.cinemaproject.opinion.Opinion;
import monaditto.cinemaproject.screening.Screening;

import java.util.Set;

@Entity
@Table(name = Movie.TABLE_NAME)
public class Movie {

    public static final String TABLE_NAME = "movies";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int duration;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Screening> screenings;

    @ManyToMany
    @JoinTable(
            name = "movie_category",
            joinColumns = @JoinColumn(name = "movie"),
            inverseJoinColumns = @JoinColumn(name = "category")
    )
    private Set<Category> categories;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
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
