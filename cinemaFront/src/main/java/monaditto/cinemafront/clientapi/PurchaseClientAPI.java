package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.PurchaseDto;
import monaditto.cinemafront.databaseMapping.PurchaseResponseDto;
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
public class PurchaseClientAPI {
    private String endpointUrl;
    private final ObjectMapper objectMapper;

    public PurchaseClientAPI(BackendConfig backendConfig) {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        endpointUrl = backendConfig.getBaseUrl() + "/api/purchases";
    }

    private CompletableFuture<List<PurchaseResponseDto>> sendLoadPurchasesRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parsePurchaseList)
                .exceptionally(e -> {
                    System.err.println("Error loading purchases: " + e.getMessage());
                    return new ArrayList<>();
                });
    }

    public CompletableFuture<List<PurchaseResponseDto>> getPurchasesByUser(Long userId) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl + "/user/" + userId);
        return sendLoadPurchasesRequest(client, request);
    }

    private List<PurchaseResponseDto> parsePurchaseList(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing purchase list: " + e.getMessage(), e);
        }
    }

    public CompletableFuture<ResponseResult> createPurchase(PurchaseDto purchaseDto) {
        HttpClient client = HttpClient.newHttpClient();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(purchaseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = RequestBuilder.buildRequestPOST(endpointUrl, jsonString);
        return sendCreatePurchaseRequest(client, request);
    }

    public CompletableFuture<ResponseResult> confirmPurchase(Long purchaseId) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestPOST(endpointUrl + "/" + purchaseId + "/confirm", "");
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error " + e.getMessage()));
    }

    public CompletableFuture<ResponseResult> cancelPurchase(Long purchaseId) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestPOST(endpointUrl + "/" + purchaseId + "/cancel", "");
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error " + e.getMessage()));
    }

    private CompletableFuture<ResponseResult> sendCreatePurchaseRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error " + e.getMessage()));
    }
}
