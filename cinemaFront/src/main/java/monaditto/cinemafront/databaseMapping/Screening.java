package monaditto.cinemafront.databaseMapping;

import java.time.LocalDateTime;

public class Screening {

    private Long id;

    private Movie movie;

    private MovieRoom room;

    private LocalDateTime start;

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