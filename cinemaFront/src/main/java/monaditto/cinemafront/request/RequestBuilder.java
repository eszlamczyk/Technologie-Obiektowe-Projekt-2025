package monaditto.cinemafront.request;

import java.net.URI;
import java.net.http.HttpRequest;

public class RequestBuilder {

    private static String jwtToken = null;

    // Method to set the JWT token
    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    private static HttpRequest.Builder addHeaders(HttpRequest.Builder builder) {
        builder.header("Content-Type", "application/json");
        if (jwtToken != null && !jwtToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }
        return builder;
    }

    public static HttpRequest buildRequestGET(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        return addHeaders(builder).build();
    }

    public static HttpRequest buildRequestPUT(String url, String jsonBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
        return addHeaders(builder).build();
    }

    public static HttpRequest buildRequestDELETE(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE();
        return addHeaders(builder).build();
    }

    public static HttpRequest buildRequestPOST(String url, String jsonBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        return addHeaders(builder).build();
    }
}
