package monaditto.cinemaproject.opinion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OpinionRepository extends JpaRepository<Opinion, OpinionId> {
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    List<Opinion> findByMovieId(Long movieId);

    List<Opinion> findByUserId(Long userId);


    @Query("SELECT AVG(o.rating) FROM Opinion o WHERE o.movie.id = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") Long movieId);
}
