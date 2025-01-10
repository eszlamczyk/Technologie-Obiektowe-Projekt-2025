package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.DTO.AuthResponse;
import monaditto.cinemafront.controller.DTO.LoginRequest;
import monaditto.cinemafront.databaseMapping.RoleDto;
import monaditto.cinemafront.request.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class LoginClientAPI {

    private String endpointUrl;

    private String baseUrl;

    private String getCategoryUrl;

    private final BackendConfig backendConfig;

    private final ObjectMapper objectMapper;

    private final HttpClient httpClient;

    @Autowired
    public LoginClientAPI(HttpClient client, BackendConfig backendConfig) {
        this.httpClient = client;
        this.backendConfig = backendConfig;
        objectMapper = new ObjectMapper();
    }

    public CompletableFuture<HttpResponse<String>> login(String email, String password){
        LoginRequest loginRequest = new LoginRequest(email,password);

        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(loginRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request =
                RequestBuilder.buildRequestPOST(backendConfig.getBaseUrl() + "/api/auth/login", requestBody);

        return sendLoginRequest(request);
    }

    private CompletableFuture<HttpResponse<String>> sendLoginRequest(HttpRequest request){
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

}
