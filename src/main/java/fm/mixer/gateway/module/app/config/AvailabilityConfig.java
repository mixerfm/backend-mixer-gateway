package fm.mixer.gateway.module.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("mixer-gateway.availability")
public class AvailabilityConfig {

    private List<String> supportedCountries;
    private List<EndpointConfig> excludedEndpoints;

    @Getter
    @Setter
    public static class EndpointConfig {

        private String path;
        private HttpMethod method;
    }
}
