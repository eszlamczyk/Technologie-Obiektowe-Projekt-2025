package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.MovieWithCategoriesDto;
import monaditto.cinemafront.databaseMapping.ScreeningDto;
import monaditto.cinemafront.request.RequestBuilder;
import monaditto.cinemafront.response.ResponseResult;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class ScreeningClientAPI {

    private String endpointUrl;

    private String deleteUrl;

    private String createUrl;

    private String editUrl;

    private String baseUrl;

    private final ObjectMapper objectMapper;

    private final BackendConfig backendConfig;

    public ScreeningClientAPI(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        initializeUrls();
    }

    private void initializeUrls() {
        baseUrl = backendConfig.getBaseUrl();
        endpointUrl = baseUrl + "/api/screenings";
        deleteUrl = endpointUrl;
        createUrl = endpointUrl;
        editUrl = endpointUrl;
    }

    public CompletableFuture<List<ScreeningDto>> loadScreenings() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl);

        return sendLoadScreeningsRequest(client, request);
    }

    private CompletableFuture<List<ScreeningDto>> sendLoadScreeningsRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseScreenings)
                .exceptionally(e -> {
                    System.err.println("Error loading the movies: " + e.getMessage());
                    return new ArrayList<>();
                });
    }

    private List<ScreeningDto> parseScreenings(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing screening list: " + e.getMessage(), e);
        }
    }

    public CompletableFuture<List<ScreeningDto>> loadUpcomingScreenings() {
        HttpClient client = HttpClient.newHttpClient();

        String urlWithParam = getRequestWithDate();

        HttpRequest request = RequestBuilder.buildRequestGET(urlWithParam);

        return sendLoadUpcomingScreeningsRequest(client, request);
    }

    private String getRequestWithDate() {
        LocalDateTime dateTime = LocalDateTime.now();

        String formattedDateTime = URLEncoder.encode(
                dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                StandardCharsets.UTF_8
        );

        return endpointUrl + "/upcoming?dateTime=" + formattedDateTime;
    }

    private CompletableFuture<List<ScreeningDto>> sendLoadUpcomingScreeningsRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseScreenings)
                .exceptionally(e -> {
                    System.err.println("Error loading the movies: " + e.getMessage());
                    return new ArrayList<>();
                });
    }

    public int delete(ScreeningDto toDelete) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestDELETE(deleteUrl + "/" + toDelete.id());

        return sendDeleteScreeningRequest(client, request);
    }

    private int sendDeleteScreeningRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .exceptionally(e -> {
                    System.err.println("Error deleting the movie: " + e.getMessage());
                    return null;
                })
                .join();
    }

    private String serializeWrapperDto(ScreeningDto wrapperDto) {
        try {
            return objectMapper.writeValueAsString(wrapperDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ResponseResult> createScreening(ScreeningDto screeningDto) {
        String jsonBody = serializeWrapperDto(screeningDto);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestPUT(createUrl, jsonBody);

        return sendCreateScreeningRequest(client, request);
    }

    private CompletableFuture<ResponseResult> sendCreateScreeningRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> {
                    System.err.println("Error creating screening: " + e.getMessage());
                    return new ResponseResult(500, "Error " + e.getMessage());
                });
    }

    public CompletableFuture<ResponseResult> editScreening(Long screeningId, ScreeningDto newScreeningDto) {
        String jsonBody = serializeWrapperDto(newScreeningDto);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestPUT(editUrl + "/" + screeningId, jsonBody);

        return sendEditScreeningRequest(client, request);
    }

    private CompletableFuture<ResponseResult> sendEditScreeningRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> {
                    System.err.println("Error editing the screening: " + e.getMessage());
                    return new ResponseResult(500, "Error " + e.getMessage());
                });
    }
}
