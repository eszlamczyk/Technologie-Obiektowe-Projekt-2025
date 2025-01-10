package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.MovieWithCategoriesDto;
import monaditto.cinemafront.request.RequestBuilder;
import monaditto.cinemafront.response.ResponseResult;
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

    private String createUrl;

    private String editUrl;

    private String categoriesUrl;

    private String baseUrl;

    private final ObjectMapper objectMapper;

    private final CategoryClientAPI categoryClientAPI;

    private final BackendConfig backendConfig;

    private final HttpClient httpClient;

    public MovieClientAPI(BackendConfig backendConfig, CategoryClientAPI categoryClientAPI, HttpClient httpClient) {
        this.backendConfig = backendConfig;
        this.categoryClientAPI = categoryClientAPI;
        this.httpClient = httpClient;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        initializeUrls();
    }

    private void initializeUrls() {
        baseUrl = backendConfig.getBaseUrl();
        endpointUrl = baseUrl + "/api/movies";
        deleteUrl = endpointUrl + "/delete";
        createUrl = endpointUrl + "/create";
        editUrl = endpointUrl + "/edit";
        categoriesUrl = endpointUrl + "/categories";
    }

    public CompletableFuture<ResponseResult> createMovie(MovieDto movieDto, List<CategoryDto> categories) {
        MovieWithCategoriesDto wrapperDto = new MovieWithCategoriesDto(movieDto, categories);
        String jsonBody = serializeWrapperDto(wrapperDto);

        HttpRequest request = RequestBuilder.buildRequestPUT(createUrl, jsonBody);

        return sendCreateMovieRequest(httpClient, request);
    }

    private CompletableFuture<ResponseResult> sendCreateMovieRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> {
                    System.err.println("Error loading the movies: " + e.getMessage());
                    return new ResponseResult(500, "Error " + e.getMessage());
                });
    }

    public CompletableFuture<ResponseResult> editMovie(Long movieId, MovieDto newMovieDto, List<CategoryDto> categories) {
        MovieWithCategoriesDto wrapperDto = new MovieWithCategoriesDto(newMovieDto, categories);
        String jsonBody = serializeWrapperDto(wrapperDto);

        HttpRequest request = RequestBuilder.buildRequestPUT(editUrl + "/" + movieId, jsonBody);

        return sendEditMovieRequest(httpClient, request);
    }

    private CompletableFuture<ResponseResult> sendEditMovieRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> {
                    System.err.println("Error editing the movies: " + e.getMessage());
                    return new ResponseResult(500, "Error " + e.getMessage());
                });
    }

    public CompletableFuture<List<CategoryDto>> getMovieCategories(MovieDto movieDto) {
        HttpRequest request = RequestBuilder.buildRequestGET(categoriesUrl + "/" + movieDto.id());

        return sendGetMovieCategoriesRequest(httpClient, request);
    }

    private CompletableFuture<List<CategoryDto>> sendGetMovieCategoriesRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(categoryClientAPI::parseCategoryList)
                .exceptionally(e -> {
                    System.err.println("Error loading the movies: " + e.getMessage());
                    return List.of();
                });
    }

    public CompletableFuture<List<MovieDto>> loadMovies() {
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl);

        return sendLoadMoviesRequest(httpClient, request);
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

    private String serializeWrapperDto(MovieWithCategoriesDto wrapperDto) {
        try {
            return objectMapper.writeValueAsString(wrapperDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(MovieDto movieDto) {
        HttpRequest request = RequestBuilder.buildRequestDELETE(deleteUrl + "/" + movieDto.id());

        return sendDeleteMovieRequest(httpClient, request);
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
