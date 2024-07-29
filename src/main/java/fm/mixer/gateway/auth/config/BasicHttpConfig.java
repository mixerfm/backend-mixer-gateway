package fm.mixer.gateway.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mixer-gateway.security.http-basic")
public class BasicHttpConfig {

    private boolean enabled = false;
    private List<ClientConfig> clients;

    @Getter
    @Setter
    public static class ClientConfig {

        private String username;
        private String password;
        private List<String> roles = List.of();
    }
}
