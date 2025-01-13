package monaditto.cinemaproject.opinion;

import jakarta.persistence.EntityNotFoundException;
import monaditto.cinemaproject.movie.Movie;
import monaditto.cinemaproject.movie.MovieRepository;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OpinionService {

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    public void addOpinion(OpinionDto opinionDto) {
        boolean exists = opinionRepository.existsByUserIdAndMovieId(opinionDto.userId(), opinionDto.movieId());

        if (exists) {
            throw new IllegalArgumentException("User has already submitted an opinion for this movie.");
        }

        LocalDate date = LocalDate.now();

        boolean isReleased = movieRepository.existsByIdAndReleaseDateBefore(opinionDto.movieId(), date);

        if (!isReleased) {
            throw new IllegalArgumentException("Wait until this movie will be released!");
        }

        User user = userRepository.findById(opinionDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Movie movie = movieRepository.findById(opinionDto.movieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        Opinion opinion = new Opinion(user, movie, opinionDto.rating(), opinionDto.comment());
        opinionRepository.save(opinion);
    }

    public OpinionDto getOpinion(Long userId, Long movieId) {
        OpinionId opinionId = getOpinionId(userId, movieId);

        return opinionRepository.findById(opinionId)
                .map(OpinionDto::opinionToOpinionDto)
                .orElse(null);
    }

    private OpinionId getOpinionId(Long userId, Long movieId) {
        return new OpinionId(new User(userId), new Movie(movieId));
    }

    public List<OpinionDto> getOpinionsForMovie(Long movieId) {
        List<Opinion> opinions = opinionRepository.findByMovieId(movieId);
        return opinions.stream().map(OpinionDto::opinionToOpinionDto).toList();
    }

    public void updateOpinion(Long userId, Long movieId, OpinionDto opinionDTO) {
        OpinionId opinionId = getOpinionId(userId, movieId);
        Opinion opinion = opinionRepository.findById(opinionId)
                .orElseThrow(() -> new IllegalArgumentException("Opinion not found"));

        opinion.setRating(opinionDTO.rating());
        opinion.setComment(opinionDTO.comment());

        opinionRepository.save(opinion);
    }

    public void deleteOpinion(Long userId, Long movieId) {
        if (!opinionRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new IllegalArgumentException("Opinion not found.");
        }

        OpinionId opinionId = getOpinionId(userId, movieId);
        opinionRepository.deleteById(opinionId);
    }

    public List<OpinionDto> getOpinionsForUser(Long userId) {
        List<Opinion> opinions = opinionRepository.findByUserId(userId);
        return opinions.stream().map(OpinionDto::opinionToOpinionDto).toList();
    }
}
