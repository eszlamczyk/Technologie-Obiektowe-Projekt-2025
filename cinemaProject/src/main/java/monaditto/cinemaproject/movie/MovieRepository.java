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
            "JOIN m.categories c " +
            "JOIN m.screenings s " +
            "WHERE s.start > :localDateTime " +
            "AND NOT EXISTS (" +
            "    SELECT 1 " +
            "    FROM Category cat " +
            "    WHERE cat.id = :categoryId " +
            "    AND cat MEMBER OF m.categories" +
            ") " +
            "AND NOT EXISTS (" +
            "    SELECT p" +
            "    FROM Purchase p" +
            "    JOIN p.screening ps" +
            "    WHERE p.user.id = :userId" +
            "    AND ps.movie.id = m.id" +
            "    AND p.reservationStatus = 'PAID'" +
            ")")
    List<Movie> findOtherMoviesNotWatchedByUser(@Param("categoryId") Long categoryId,
                                                @Param("userId") Long userId,
                                                @Param("localDateTime") LocalDateTime localDateTime);

    @Query("SELECT m, AVG(o.rating) FROM Movie m " +
            "JOIN Opinion o ON o.movie.id = m.id " +
            "GROUP BY m " +
            "ORDER BY AVG(o.rating) DESC")
    List<Object[]> findTopRatedMovies();

    @Query("SELECT DISTINCT m " +
            "FROM Movie m " +
            "JOIN m.categories c " +
            "JOIN m.screenings s " +
            "WHERE c.id = :categoryId " +
            "AND s.start > :localDateTime " +
            "AND NOT EXISTS (" +
            "    SELECT p" +
            "    FROM Purchase p" +
            "    JOIN p.screening ps" +
            "    WHERE p.user.id = :userId" +
            "    AND ps.movie.id = m.id" +
            "    AND p.reservationStatus = 'PAID'" +
            ")")
    List<Movie> findMoviesByCategoryAndNotWatchedByUser(@Param("categoryId") Long categoryId,
                                                        @Param("userId") Long userId,
                                                        @Param("localDateTime") LocalDateTime localDateTime);

    boolean existsByIdAndReleaseDateBefore(Long aLong, LocalDate date);
}
