package monaditto.cinemaproject.opinion;

import monaditto.cinemaproject.movie.Movie;
import monaditto.cinemaproject.user.User;

import java.io.Serializable;
import java.util.Objects;

public class OpinionId implements Serializable {

    private final User user;

    private final Movie movie;

    public OpinionId(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpinionId that = (OpinionId) o;
        return Objects.equals(user, that.user) && Objects.equals(movie, that.movie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, movie);
    }
}
