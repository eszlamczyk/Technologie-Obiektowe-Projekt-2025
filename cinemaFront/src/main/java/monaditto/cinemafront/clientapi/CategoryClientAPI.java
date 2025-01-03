package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.databaseMapping.CategoryDto;
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
public class CategoryClientAPI {

    private String endpointUrl;

    private String baseUrl;

    private String getCategoryUrl;

    private final BackendConfig backendConfig;

    private final ObjectMapper objectMapper;

    public CategoryClientAPI(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
        objectMapper = new ObjectMapper();
        initializeUrls();
    }

    private void initializeUrls() {
        baseUrl = backendConfig.getBaseUrl();
        endpointUrl = baseUrl + "/api/categories";
        getCategoryUrl = endpointUrl + "/category";
    }

    public CompletableFuture<List<CategoryDto>> loadCategories() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl);

        return sendLoadCategoriesRequest(client, request);
    }

    private CompletableFuture<List<CategoryDto>> sendLoadCategoriesRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseCategoryList)
                .exceptionally(e -> {
                    System.err.println("Error loading the categories: " + e.getMessage());
                    return new ArrayList<>();
                });
    }

    public List<CategoryDto> parseCategoryList(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing category list: " + e.getMessage(), e);
        }
    }

    public CompletableFuture<ResponseResult> createCategory(CategoryDto categoryDto){
        HttpClient client = HttpClient.newHttpClient();

        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(categoryDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = RequestBuilder.buildRequestPOST(endpointUrl,jsonString);

        return sendCreateCategoryRequest(client,request);
    }

    private CompletableFuture<ResponseResult> sendCreateCategoryRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error " + e.getMessage()));
    }

    public CompletableFuture<ResponseResult> editCategory(Long categoryId, CategoryDto newCategoryDto){
        HttpClient client = HttpClient.newHttpClient();

        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(newCategoryDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = RequestBuilder.buildRequestPUT(endpointUrl + "/" + categoryId ,jsonString);

        return sendEditMovieRequest(client,request);
    }

    private CompletableFuture<ResponseResult> sendEditMovieRequest(HttpClient client, HttpRequest request){
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(),response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error" + e.getMessage()));
    }

    public CompletableFuture<ResponseResult> deleteOneCategory(Long categoryId){
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = RequestBuilder.buildRequestDELETE(this.endpointUrl + "/" + categoryId);

        return sendDeleteRequest(client, request);
    }

    private CompletableFuture<ResponseResult> sendDeleteRequest(HttpClient client, HttpRequest request){
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ResponseResult(response.statusCode(), response.body()))
                .exceptionally(e -> new ResponseResult(500, "Error" + e.getMessage()));
    }

}
