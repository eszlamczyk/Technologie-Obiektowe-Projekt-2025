package monaditto.cinemafront.request;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestBuilder {

    private static String sessionId;

    public static void setSessionId(String id){
        sessionId = id;
    }

    private static HttpRequest.Builder addHeaders(HttpRequest.Builder builder) {
        builder.header("Content-Type", "application/json");
        if (sessionId != null && !sessionId.isEmpty()) {
            builder.header("Cookie", "JSESSIONID=" + sessionId);
        } else {
            System.err.println("No session ID available");
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

    public static void extractAndSetSessionId(HttpResponse<?> response) {
        response.headers()
                .allValues("Set-Cookie")
                .stream()
                .filter(cookie -> cookie.startsWith("JSESSIONID="))
                .findFirst()
                .ifPresent(cookie -> {
                    String id = cookie.split(";")[0].replace("JSESSIONID=", "");
                    setSessionId(id);
                });
    }
}