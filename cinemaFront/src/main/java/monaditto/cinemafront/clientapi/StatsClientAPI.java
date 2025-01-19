package monaditto.cinemafront.clientapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monaditto.cinemafront.config.BackendConfig;
import monaditto.cinemafront.controller.admin.StatsPanel;
import monaditto.cinemafront.databaseMapping.CategoryDto;
import monaditto.cinemafront.databaseMapping.MovieWithEarningsDto;
import monaditto.cinemafront.databaseMapping.PeriodType;
import monaditto.cinemafront.request.RequestBuilder;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class StatsClientAPI {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final String endpointUrl;

    private final BackendConfig backendConfig;

    private static final String REVENUE_ENDPOINT = "revenue/";
    private static final String MOVIE_ENDPOINT = "most-popular-movie/";
    private static final String CATEGORY_ENDPOINT = "most-popular-category/";
    private static final String ATTENDANCE_ENDPOINT = "average-attendance/";

    public StatsClientAPI(HttpClient client, BackendConfig backendConfig) {
        this.httpClient = client;
        this.backendConfig = backendConfig;
        objectMapper = new ObjectMapper();
        endpointUrl = backendConfig.getBaseUrl() + "/api/statistics/";
    }

    public double getRevenueForPeriod(PeriodType periodType) {
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl + REVENUE_ENDPOINT + periodType);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return Double.parseDouble(response.body());
                    } else {
                        throw new RuntimeException("Failed to fetch revenue: " + response.body());
                    }
                }).join();
    }

    public MovieWithEarningsDto getMostPopularMovieForPeriod(PeriodType periodType) {
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl + MOVIE_ENDPOINT + periodType.name());
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.body() == null || response.body().isEmpty()) {
                        return new MovieWithEarningsDto(0L, "Nie ma popularnego filmu", 0.0);
                    }
                    try {
                        return objectMapper.readValue(response.body(), MovieWithEarningsDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing movie data: " + periodType + " " + e.getMessage(), e);
                    }
                })
                .join();
    }

    public CategoryDto getMostPopularCategoryForPeriod(PeriodType periodType) {
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl + CATEGORY_ENDPOINT + periodType);
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), CategoryDto.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error parsing category data: " + e.getMessage(), e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch popular category: " + response.body());
                    }
                }).join();
    }

    public double getAverageAttendanceForPeriod(PeriodType periodType) {
        HttpRequest request = RequestBuilder.buildRequestGET(endpointUrl + ATTENDANCE_ENDPOINT + periodType);
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return Double.parseDouble(response.body());
                    } else {
                        throw new RuntimeException("Failed to fetch average attendance: " + response.body());
                    }
                }).join();
    }
}
