package monaditto.cinemaproject.movieRoom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import monaditto.cinemaproject.screening.Screening;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = MovieRoom.TABLE_NAME)
public class MovieRoom {

    public static final String TABLE_NAME = "movie_rooms";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_room_id")
    private Long id;

    @Column(name = "movie_room_name", nullable = false)
    private String movieRoomName;

    @Column(name = "max_seats", nullable = false)
    private int maxSeats;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonIgnore
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
