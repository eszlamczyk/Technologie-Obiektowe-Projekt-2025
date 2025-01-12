package monaditto.cinemaproject.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Long> {

    @Query("SELECT m FROM Movie m WHERE m.releaseDate > :today ORDER BY m.releaseDate")
    List<Movie> findComingSoonMovies(@Param("today") LocalDate today);

    @Query("SELECT m FROM Movie m " +
            "JOIN m.screenings s " +
            "LEFT JOIN Purchase p ON p.screening.id = s.id AND p.user.id = :userId " +
            "WHERE p.id IS NULL")
    List<Movie> findMoviesNotWatchedByUser(@Param("userId") Long userId);

    @Query("SELECT m, AVG(o.rating) FROM Movie m " +
            "JOIN Opinion o ON o.movie.id = m.id " +
            "GROUP BY m " +
            "ORDER BY AVG(o.rating) DESC")
    List<Object[]> findTopRatedMovies();

    @Query("SELECT m FROM Movie m " +
            "JOIN m.categories c " +
            "JOIN m.screenings s " +
            "LEFT JOIN Purchase p ON p.screening.id = s.id AND p.user.id = :userId AND p.reservationStatus = 'PAID' " +
            "WHERE c.id = :categoryId " +
            "AND s.start > :localDateTime " +
            "AND p.id IS NULL")
    List<Movie> findMoviesByCategoryAndNotWatchedByUser(@Param("categoryId") Long categoryId,
                                                        @Param("userId") Long userId,
                                                        @Param("localDateTime") LocalDateTime localDateTime);

}
