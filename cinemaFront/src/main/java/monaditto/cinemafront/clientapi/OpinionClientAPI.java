package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.MovieDto;
import monaditto.cinemafront.databaseMapping.OpinionDto;
import monaditto.cinemafront.request.RequestBuilder;
import monaditto.cinemafront.response.ResponseResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
public class OpinionClientAPI {

    private String baseUrl;

    private String endpointUrl;

    private final ObjectMapper objectMapper;

    private final BackendConfig backendConfig;

    private final HttpClient httpClient;

    public OpinionClientAPI(BackendConfig backendConfig, HttpClient httpClient) {
        this.backendConfig = backendConfig;
        this.httpClient = httpClient;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        initializeUrls();
    }

    private void initializeUrls() {
        baseUrl = backendConfig.getBaseUrl();
        endpointUrl = baseUrl + "/api/opinions";
    }

    public CompletableFuture<ResponseResult> addOpinion(OpinionDto opinionDto) {
        String jsonBody = serializeOpinionDto(opinionDto);

        HttpRequest request = RequestBuilder.buildRequestPOST(endpointUrl, jsonBody);

        return sendAddOpinionRequest(request);
    }

    private CompletableFuture<ResponseResult> sendAddOpinionRequest(HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> {
                    System.err.println("Error submitting opinion: " + e.getMessage());
                    return new ResponseResult(500, "Error " + e.getMessage());
                });
    }

    private String serializeOpinionDto(OpinionDto opinionDto) {
        try {
            return objectMapper.writeValueAsString(opinionDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize opinion", e);
        }
    }

    public CompletableFuture<List<OpinionDto>> getUserOpinions(Long userId) {
        HttpRequest request = RequestBuilder.buildRequestGET(
                endpointUrl + "/user/" + userId
        );

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    new TypeReference<List<OpinionDto>>() {});
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error parsing opinions", e);
                        }
                    } else {
                        throw new RuntimeException("Failed to load opinions: " + response.body());
                    }
                });
    }

    public int delete(OpinionDto opinionDto) {
        HttpRequest request = RequestBuilder.buildRequestDELETE(
                endpointUrl + "/" + opinionDto.userId() + "/" + opinionDto.movieId()
        );

        return sendDeleteOpinionRequest(httpClient, request);
    }

    private int sendDeleteOpinionRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .exceptionally(e -> {
                    System.err.println("Error deleting the opinion: " + e.getMessage());
                    return null;
                })
                .join();
    }

    public CompletableFuture<ResponseResult> editOpinion(OpinionDto opinionDto) {
        String jsonBody = serializeOpinionDto(opinionDto);

        HttpRequest request = RequestBuilder.buildRequestPUT(endpointUrl + "/" + opinionDto.userId() + "/" + opinionDto.movieId(), jsonBody);

        return sendEditOpinionRequest(request);
    }

    private CompletableFuture<ResponseResult> sendEditOpinionRequest(HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> {
                    System.err.println("Error Editing opinion: " + e.getMessage());
                    return new ResponseResult(500, "Error " + e.getMessage());
                });
    }
}
