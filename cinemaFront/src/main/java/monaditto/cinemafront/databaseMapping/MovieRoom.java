package monaditto.cinemafront.databaseMapping;


import java.util.HashSet;
import java.util.Set;

public class MovieRoom {

    private Long id;

    private String movieRoomName;

    private int maxSeats;

    private Set<Screening> screenings = new HashSet<>();

    public MovieRoom() {}

    public MovieRoom(String movieRoomName, int maxSeats) {
        this.movieRoomName = movieRoomName;
        this.maxSeats = maxSeats;
    }

    public Long getMovieRoomId() {
        return id;
    }

    public String getMovieRoomName() {
        return movieRoomName;
    }

    public void setMovieRoomName(String movieRoomName) {
        this.movieRoomName = movieRoomName;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public Set<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(Set<Screening> screenings) {
        this.screenings = screenings;
    }

    public void addScreening(Screening screening) {
        screenings.add(screening);
        screening.setRoom(this);
    }
}
