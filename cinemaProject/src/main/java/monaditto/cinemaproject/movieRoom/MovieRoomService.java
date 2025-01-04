package monaditto.cinemaproject.movieRoom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieRoomService {

    private final MovieRoomRepository movieRoomRepository;

    @Autowired
    public MovieRoomService(MovieRoomRepository movieRoomRepository) {
        this.movieRoomRepository = movieRoomRepository;
    }

    public MovieRoom save(MovieRoomDto movieRoomDto) {
        MovieRoom movieRoom = new MovieRoom(movieRoomDto.movieRoomName(), movieRoomDto.maxSeats());
        return movieRoomRepository.save(movieRoom);
    }

    public MovieRoomCreateStatus createMovieRoom(MovieRoomDto movieRoomDto) {
        if (movieRoomRepository.findByMovieRoomName(movieRoomDto.movieRoomName()).isPresent()) {
            return MovieRoomCreateStatus.MOVIE_ROOM_NAME_TAKEN;
        }
        MovieRoom movieRoom =  new MovieRoom(
                movieRoomDto.movieRoomName(),
                movieRoomDto.maxSeats());

        movieRoomRepository.save(movieRoom);
        return MovieRoomCreateStatus.SUCCESS;
    }

    public Optional<MovieRoomDto> getMovieRoom(String movieRoomName) {
        return movieRoomRepository.findByMovieRoomName(movieRoomName)
                .map(MovieRoomDto::movieRoomtoMovieRoomDto);
    }

    public List<MovieRoomDto> getAllMovieRooms() {
        return movieRoomRepository.findAll()
                .stream().map(MovieRoomDto::movieRoomtoMovieRoomDto).collect(Collectors.toList());
    }

    public MovieRoomCreateStatus editMovieRoom(Long id, MovieRoomDto movieRoomDto) {
        Optional<MovieRoom> movieRoomWithTheName =
                movieRoomRepository.findByMovieRoomName(movieRoomDto.movieRoomName());

        if (movieRoomWithTheName.isPresent() &&
                (movieRoomWithTheName.get().getMovieRoomName().equals(movieRoomDto.movieRoomName()) &&
                    !Objects.equals(movieRoomWithTheName.get().getMovieRoomId(), id))) {
            return MovieRoomCreateStatus.MOVIE_ROOM_NAME_TAKEN;
        }

        Optional<MovieRoom> optionalMovieRoom = movieRoomRepository.findById(id);
        if(optionalMovieRoom.isPresent()) {

            MovieRoom movieRoom = optionalMovieRoom.get();
            movieRoom.setMovieRoomName(movieRoomDto.movieRoomName());
            movieRoom.setMaxSeats(movieRoomDto.maxSeats());
            movieRoomRepository.save(movieRoom);
            return MovieRoomCreateStatus.SUCCESS;
        }
        return MovieRoomCreateStatus.INCORRECT_ID;
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
}
