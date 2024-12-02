package monaditto.cinemaproject.screening;

import jakarta.persistence.*;
import monaditto.cinemaproject.movie.Movie;
import monaditto.cinemaproject.movieRoom.MovieRoom;

import java.time.LocalDateTime;

@Entity
@Table(name = Screening.TABLE_NAME)
public class Screening {

    public static final String TABLE_NAME = "screenings";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private MovieRoom room;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private Double price;

    public Screening() {}

    public Screening(Movie movie, MovieRoom room, LocalDateTime start, Double price) {
        this.movie = movie;
        this.room = room;
        this.start = start;
        this.price = price;
    }

    public Long getScreeningId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public MovieRoom getRoom() {
        return room;
    }

    public void setRoom(MovieRoom room) {
        this.room = room;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}