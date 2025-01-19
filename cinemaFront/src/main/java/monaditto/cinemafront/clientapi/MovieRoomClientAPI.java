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

    private final HttpClient httpClient;

    public MovieRoomClientAPI(BackendConfig backendConfig, HttpClient httpClient) {
        this.backendConfig = backendConfig;
        this.httpClient = httpClient;
        objectMapper = new ObjectMapper();
        initializeUrls();
    }

    private void initializeUrls() {
        baseUrl = backendConfig.getBaseUrl();
        endpointUrl = baseUrl + "/api/movieRooms";
    }

    public CompletableFuture<List<MovieRoomDto>> loadMovieRooms() {
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl);

        return sendLoadMovieRoomsRequest(httpClient, request);
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


        HttpRequest request = RequestBuilder.buildRequestDELETE(this.endpointUrl + "/" + categoryId);

        return sendDeleteRequest(httpClient, request);
    }

    private CompletableFuture<ResponseResult> sendDeleteRequest(HttpClient client, HttpRequest request){
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error" + e.getMessage()));
    }

    public CompletableFuture<ResponseResult> createMovieRoom(MovieRoomDto movieRoomDto){

        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(movieRoomDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = RequestBuilder.buildRequestPOST(endpointUrl,jsonString);

        return sendCreateMovieRoomRequest(httpClient,request);
    }

    public CompletableFuture<ResponseResult> editMovieRoom(Long movieRoomId, MovieRoomDto newMovieRoomDto){

        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(newMovieRoomDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = RequestBuilder.buildRequestPUT(endpointUrl + "/" + movieRoomId ,jsonString);

        return sendEditMovieRequest(httpClient,request);
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
