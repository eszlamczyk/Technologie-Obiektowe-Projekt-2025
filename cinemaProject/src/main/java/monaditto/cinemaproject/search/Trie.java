package monaditto.cinemaproject.search;

import monaditto.cinemaproject.movie.Movie;
import monaditto.cinemaproject.movie.MovieDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();

    List<MovieDto> movies = new ArrayList<>();
}

@Component
public class Trie {

    private final TrieNode root = new TrieNode();

    public void insert(MovieDto movie) {
        String title = movie.title();
        TrieNode current = root;
        for (char c : title.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.movies.add(movie);
    }

    public List<MovieDto> search(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return new ArrayList<>();
            }
        }
        return collectMovies(current);
    }

    private List<MovieDto> collectMovies(TrieNode node) {
        List<MovieDto> result = new ArrayList<>(node.movies);
        for (TrieNode child : node.children.values()) {
            result.addAll(collectMovies(child));
        }
        return result;
    }

    public void remove(MovieDto movieDto) {
        TrieNode current = root;
        String title = movieDto.title().toLowerCase();

        for (int i = 0; i < title.length(); i++) {
            char currentChar = title.charAt(i);

            if (current == null || current.children.get(currentChar) == null) {
                return;
            }
            current = current.children.get(currentChar);

            current.movies.removeIf(movie -> movie.id().equals(movieDto.id()));

            if (current.movies.isEmpty() && current.children.isEmpty()) {
                current = null;
            }
        }
    }
}
