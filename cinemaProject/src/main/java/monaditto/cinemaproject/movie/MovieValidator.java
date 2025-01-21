package monaditto.cinemaproject.movie;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class MovieValidator {

    public CreateMovieStatus validateMovieDto(MovieDto movieDto) {
        if (!validateString(movieDto.title()) ||
                !validateString(movieDto.description()) ||
                movieDto.duration() < 0) {
            return CreateMovieStatus.MISSING_DATA;
        }

        if (!validateImageUrl(movieDto.posterUrl())) {
            return CreateMovieStatus.INVALID_URL;
        }

        return CreateMovieStatus.SUCCESS;
    }

    private boolean validateImageUrl(String url) {
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType();

            return responseCode == HttpURLConnection.HTTP_OK &&
                    contentType.startsWith("image/");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateString(String string) {
        return string != null &&
                !string.isEmpty() &&
                !string.startsWith(" ") &&
                !string.endsWith(" ");
    }
}
