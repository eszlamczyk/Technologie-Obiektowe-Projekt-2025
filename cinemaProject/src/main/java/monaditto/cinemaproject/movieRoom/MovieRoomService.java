package monaditto.cinemaproject.movieRoom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovieRoomService {

    private final MovieRoomRepository movieRoomRepository;

    @Autowired
    public MovieRoomService(MovieRoomRepository movieRoomRepository) {
        this.movieRoomRepository = movieRoomRepository;
    }

    public MovieRoom save(MovieRoom movieRoom) {
        return movieRoomRepository.save(movieRoom);
    }

    public boolean createMovieRoom(MovieRoomDto movieRoomDto) {
        if (movieRoomRepository.findByMovieRoomName(movieRoomDto.movieRoomName()).isPresent()) {
            return false;
        }
        MovieRoom movieRoom =  new MovieRoom(
                movieRoomDto.movieRoomName(),
                movieRoomDto.maxSeats());

        movieRoomRepository.save(movieRoom);
        return true;
    }

    public Optional<MovieRoom> getMovieRoom(String movieRoomName) {
        return movieRoomRepository.findByMovieRoomName(movieRoomName);
    }

    public List<MovieRoom> getAllMovieRooms() {
        return movieRoomRepository.findAll();
    }

    public boolean editMovieRoom(Long id, MovieRoomDto movieRoomDto) {
        Optional<MovieRoom> movieRoomWithTheName =
                movieRoomRepository.findByMovieRoomName(movieRoomDto.movieRoomName());

        if (movieRoomWithTheName.isPresent() &&
                (movieRoomWithTheName.get().getMovieRoomName().equals(movieRoomDto.movieRoomName()) &&
                    !Objects.equals(movieRoomWithTheName.get().getMovieRoomId(), id))) {
            return false;
        }

        Optional<MovieRoom> optionalMovieRoom = movieRoomRepository.findById(id);
        if(optionalMovieRoom.isPresent()) {

            MovieRoom movieRoom = optionalMovieRoom.get();
            movieRoom.setMovieRoomName(movieRoomDto.movieRoomName());
            movieRoom.setMaxSeats(movieRoomDto.maxSeats());
            return true;
        }
        return false;
    }

    public boolean deleteMovieRoom(Long id) {
        Optional<MovieRoom> optionalMovieRoom = movieRoomRepository.findById(id);
        if(optionalMovieRoom.isPresent()) {
            MovieRoom movieRoom = optionalMovieRoom.get();
            movieRoomRepository.delete(movieRoom);
            return true;
        }
        return false;
    }

    public List<String> deleteMovieRooms(List<Long> ids) {
        List<String> successfullyDeletedMovieRooms = new ArrayList<>();
        for (Long id : ids) {
            Optional<MovieRoom> optionalMovieRoom = movieRoomRepository.findById(id);
            if (optionalMovieRoom.isPresent()) {
                MovieRoom movieRoom = optionalMovieRoom.get();
                movieRoomRepository.delete(movieRoom);
                successfullyDeletedMovieRooms.add(movieRoom.getMovieRoomName());
            }
        }
        return successfullyDeletedMovieRooms;
    }
}
