package fm.mixer.gateway.module.app.config;

import fm.mixer.gateway.module.app.api.v1.model.ClientType;
import fm.mixer.gateway.module.app.api.v1.model.ServerApiType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties("mixer-gateway.versions")
public class VersionsConfig {

    private Map<ClientType, VersionConfig> clients;
    private Map<ServerApiType, VersionConfig> apis;

    @Getter
    @Setter
    @Configuration
    public static class VersionConfig {

        private String minVersion;
        private String currentVersion;
    }
}
