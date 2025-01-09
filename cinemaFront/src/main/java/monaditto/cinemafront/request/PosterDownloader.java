package monaditto.cinemafront.request;

import javafx.scene.image.Image;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class PosterDownloader {

    private Image poster = null;

    public boolean isPosterUrlValid(String stringUrl) {
        try {
            URL imageUrl = new URL(stringUrl);
            HttpURLConnection connection = getHttpURLConnection(imageUrl);

            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType();

            if (responseCode == HttpURLConnection.HTTP_OK && contentType.startsWith("image/")) {
                poster = new Image(imageUrl.toString());
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return false;
        }
    }

    public Image getPoster() {
        return poster;
    }

    private HttpURLConnection getHttpURLConnection(URL imageUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();
        return connection;
    }
}
