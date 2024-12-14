package monaditto.cinemaproject.screening;

import jakarta.transaction.Transactional;
import monaditto.cinemaproject.movie.Movie;
import monaditto.cinemaproject.movie.MovieRepository;
import monaditto.cinemaproject.movieRoom.MovieRoom;
import monaditto.cinemaproject.movieRoom.MovieRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScreeningService {

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRoomRepository movieRoomRepository;

    private boolean isConflict(Movie movie, MovieRoom movieRoom, LocalDateTime start) {
        int duration = movie.getDuration();
        LocalDateTime end = start.plusMinutes(duration);
        Long movieRoomId = movieRoom.getMovieRoomId();

        List<Screening> conflictScreenings = screeningRepository.findConflictingScreenings(
                movieRoomId,
                start,
                end
        );

        return !conflictScreenings.isEmpty();
    }

    private Movie getMovie(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));

    }

    private MovieRoom getMovieRoom(Long id) {
        return movieRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie room not found with id: " + id));
    }

    public Screening saveScreening(ScreeningDto screeningDto) {
        Movie movie = getMovie(screeningDto.movieId());
        MovieRoom movieRoom = getMovieRoom(screeningDto.movieRoomId());

        if (isConflict(movie, movieRoom, screeningDto.start())) {
            throw new RuntimeException();
        }

        Screening newScreening = new Screening(movie, movieRoom, screeningDto.start(), screeningDto.price());

        movie.addScreening(newScreening);
        movieRoom.addScreening(newScreening);

        return screeningRepository.save(newScreening);
    }

    public List<Screening> getAllScreenings() {
        return screeningRepository.findAll();
    }

    public Optional<Screening> getScreeningById(Long id) {
        return screeningRepository.findById(id);
    }

    public boolean deleteScreening(Long id) {
        if (screeningRepository.existsById(id)) {
            screeningRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Screening> getScreeningsByDate(LocalDate date) {
        return screeningRepository.findByStartBetween(date.atStartOfDay(), date.atTime(23, 59, 59));
    }

    public List<Screening> getUpcomingScreeningsAfter(LocalDateTime dateTime) {
        return screeningRepository.findByStartAfter(dateTime);
    }

    public Screening updateScreening(Long id, ScreeningDto screeningDto) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found with id: " + id));

        Movie movie = getMovie(screeningDto.movieId());
        MovieRoom movieRoom = getMovieRoom(screeningDto.movieRoomId());

        screening.setMovie(movie);
        screening.setRoom(movieRoom);
        screening.setPrice(screeningDto.price());
        screening.setStart(screeningDto.start());

        return screeningRepository.save(screening);
    }
}
