package monaditto.cinemafront.request;

import java.net.URI;
import java.net.http.HttpRequest;

public class RequestBuilder {

    public static HttpRequest buildRequestGET(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }
    public static HttpRequest buildRequestDELETE(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
    }
}
