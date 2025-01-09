package monaditto.cinemaproject.movieRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRoomRepository extends JpaRepository<MovieRoom, Long> {

    Optional<MovieRoom> findByMovieRoomName(String movieRoomName);

}
