package monaditto.cinemafront.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.request.RequestBuilder;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class MovieClientAPI {

    private String endpointUrl;

    private String deleteUrl;

    private final ObjectMapper objectMapper;

    private String baseUrl;

    public MovieClientAPI() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void initializeUrls() {
        endpointUrl = baseUrl + "/api/movies";
        deleteUrl = endpointUrl + "/delete";
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        initializeUrls();
    }

    public CompletableFuture<List<MovieDto>> loadMovies() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl);

        return sendLoadMoviesRequest(client, request);
    }

    private CompletableFuture<List<MovieDto>> sendLoadMoviesRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseMovieList)
                .exceptionally(e -> {
                    System.err.println("Error loading the movies: " + e.getMessage());
                    return new ArrayList<>();
                });
    }

    private List<MovieDto> parseMovieList(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing movie list: " + e.getMessage(), e);
        }
    }

    public int delete(MovieDto movieDto) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestDELETE(deleteUrl + "/" + movieDto.id());

        return sendDeleteMovieRequest(client, request);
    }

    private int sendDeleteMovieRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .exceptionally(e -> {
                    System.err.println("Error deleting the movie: " + e.getMessage());
                    return null;
                })
                .join();
    }
}
