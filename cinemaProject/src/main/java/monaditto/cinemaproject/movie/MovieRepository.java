package monaditto.cinemaproject.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Long> {

    @Query("SELECT m FROM Movie m WHERE m.releaseDate > :today ORDER BY m.releaseDate")
    List<Movie> findComingSoonMovies(@Param("today") LocalDate today);
}
