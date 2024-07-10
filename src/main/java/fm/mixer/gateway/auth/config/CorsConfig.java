package fm.mixer.gateway.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("mixer-gateway.cors")
public class CorsConfig {

    private List<String> allowedOrigins;
}
