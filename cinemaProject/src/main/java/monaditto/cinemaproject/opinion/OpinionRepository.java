package monaditto.cinemaproject.opinion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpinionRepository extends JpaRepository<Opinion, OpinionId> {
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    List<Opinion> findByMovieId(Long movieId);
}
