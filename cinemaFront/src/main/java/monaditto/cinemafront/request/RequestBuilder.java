package monaditto.cinemafront.request;

import java.net.URI;
import java.net.http.HttpRequest;

public class RequestBuilder {

    public static HttpRequest buildRequestGET(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    public static HttpRequest buildRequestPUT(String url, String jsonBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    public static HttpRequest buildRequestDELETE(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
    }

    public static HttpRequest buildRequestPOST(String url, String jsonBody){
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }
}
