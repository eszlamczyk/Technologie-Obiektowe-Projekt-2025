package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.UserDto;
import monaditto.cinemafront.request.RequestBuilder;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class UserClientAPI {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final BackendConfig backendConfig;

    private final String endpointUrl;

    public UserClientAPI(HttpClient client, BackendConfig backendConfig) {
        this.httpClient = client;
        this.backendConfig = backendConfig;
        objectMapper = new ObjectMapper();
        endpointUrl = backendConfig.getBaseUrl();
    }

    public CompletableFuture<List<UserDto>> loadUsers() {
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl + "/api/admin-panel/users");

        return sendLoadUsersRequest(request);
    }

    private CompletableFuture<List<UserDto>> sendLoadUsersRequest(HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseUsers)
                .exceptionally(e -> {
                    System.err.println("Error loading the movies: " + e.getMessage());
                    return new ArrayList<>();
                });
    }

    private List<UserDto> parseUsers(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing roles from JSON", e);
        }
    }

}
