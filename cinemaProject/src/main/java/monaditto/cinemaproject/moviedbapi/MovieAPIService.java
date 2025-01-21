package monaditto.cinemaproject.moviedbapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import monaditto.cinemaproject.movie.MovieDto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class MovieAPIService {
    private static final String API_URL = "http://www.omdbapi.com/";
    private static final String API_KEY = "cd9a8e0b";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MovieAPIService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    private String appendParams(String url, APIQuery apiQuery) {
        String yearQuery = apiQuery.year() != 0 ? "&y=" + apiQuery.year()  : "";
        return url + "&t=" + apiQuery.title().replace(" ", "+") + yearQuery;
    }

    public MovieDto fetchMovieByQuery(APIQuery apiQuery) {
        String url = API_URL + "?apikey=" + API_KEY;
        url = appendParams(url, apiQuery);

        HttpRequest request = getHttpRequest(url);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return parseMovieResponse(response.body());
        } catch (Exception e) {
            System.err.println("Failed to fetch the movie " + apiQuery.title());
            e.printStackTrace();
        }
        return null;
    }

    private static HttpRequest getHttpRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    private MovieDto parseMovieResponse(String responseBody) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        if (!jsonNode.has("Title") || jsonNode.has("Error")) {
            throw new IllegalArgumentException("Invalid response or movie not found: " + jsonNode.get("Error").asText());
        }

        String title = jsonNode.get("Title").asText();
        String description = jsonNode.has("Plot") ? jsonNode.get("Plot").asText() : "No description available";
        int duration = jsonNode.has("Runtime") && !jsonNode.get("Runtime").asText().equals("N/A")
                ? Integer.parseInt(jsonNode.get("Runtime").asText().replace(" min", ""))
                : 0;
        String posterUrl = jsonNode.has("Poster") && !jsonNode.get("Poster").asText().equals("N/A")
                ? jsonNode.get("Poster").asText()
                : null;
        LocalDate releaseDate = jsonNode.has("Released") && !jsonNode.get("Released").asText().equals("N/A")
                ? LocalDate.parse(jsonNode.get("Released").asText(), DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
                : null;

        return new MovieDto(title, description, duration, posterUrl, releaseDate);
    }
}

