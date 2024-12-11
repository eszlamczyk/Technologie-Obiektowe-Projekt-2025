package monaditto.cinemafront.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackendConfig {

    @Value("${backend.base-url}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
}
