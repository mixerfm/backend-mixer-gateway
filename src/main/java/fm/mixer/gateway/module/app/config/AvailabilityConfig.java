package fm.mixer.gateway.module.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("mixer-gateway.availability")
public class AvailabilityConfig {

    private List<String> supportedCountries;
}
