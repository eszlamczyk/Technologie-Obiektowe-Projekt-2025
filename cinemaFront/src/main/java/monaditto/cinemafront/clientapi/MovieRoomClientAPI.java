package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import monaditto.cinemafront.databaseMapping.MovieRoomDto;
import monaditto.cinemafront.request.RequestBuilder;
import monaditto.cinemafront.response.ResponseResult;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
public class MovieRoomClientAPI {

    private String baseUrl;

    private String endpointUrl;

    private final BackendConfig backendConfig;

    private final ObjectMapper objectMapper;

    public MovieRoomClientAPI(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
        objectMapper = new ObjectMapper();
        initializeUrls();
    }

    private void initializeUrls() {
        baseUrl = backendConfig.getBaseUrl();
        endpointUrl = baseUrl + "/api/movieRooms";
    }

    public CompletableFuture<List<MovieRoomDto>> loadMovieRooms() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl);

        return sendLoadMovieRoomsRequest(client, request);
    }

    private CompletableFuture<List<MovieRoomDto>> sendLoadMovieRoomsRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseCategoryList)
                .exceptionally(e -> {
                    System.err.println("Error loading the categories: " + e.getMessage());
                    return new ArrayList<>();
                });
    }

    public List<MovieRoomDto> parseCategoryList(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing category list: " + e.getMessage(), e);
        }
    }


    public CompletableFuture<ResponseResult> delete(Long categoryId){
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = RequestBuilder.buildRequestDELETE(this.endpointUrl + "/" + categoryId);

        return sendDeleteRequest(client, request);
    }

    private CompletableFuture<ResponseResult> sendDeleteRequest(HttpClient client, HttpRequest request){
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error" + e.getMessage()));
    }

    public CompletableFuture<ResponseResult> createMovieRoom(MovieRoomDto movieRoomDto){
        HttpClient client = HttpClient.newHttpClient();

        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(movieRoomDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = RequestBuilder.buildRequestPOST(endpointUrl,jsonString);

        return sendCreateMovieRoomRequest(client,request);
    }

    public CompletableFuture<ResponseResult> editMovieRoom(Long movieRoomId, MovieRoomDto newMovieRoomDto){
        HttpClient client = HttpClient.newHttpClient();

        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(newMovieRoomDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = RequestBuilder.buildRequestPUT(endpointUrl + "/" + movieRoomId ,jsonString);

        return sendEditMovieRequest(client,request);
    }

    private CompletableFuture<ResponseResult> sendEditMovieRequest(HttpClient client, HttpRequest request){
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(),response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error" + e.getMessage()));
    }

    private CompletableFuture<ResponseResult> sendCreateMovieRoomRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error " + e.getMessage()));
    }
}
