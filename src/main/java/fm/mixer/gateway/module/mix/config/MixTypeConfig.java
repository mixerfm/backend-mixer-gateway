package fm.mixer.gateway.module.mix.config;

import fm.mixer.gateway.module.mix.api.v1.model.MixType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Configuration
@ConfigurationProperties("mixer-gateway")
public class MixTypeConfig {

    private Map<MixType, TypeConfig> mixTypes;
    private static MixTypeConfig instance;

    @PostConstruct
    private void init() {
        instance = this;
    }

    @Getter
    @Setter
    @Configuration
    public static class TypeConfig {

        private Integer numberOfLikes;
        private Integer playCount;
    }

    public static Map<MixType, TypeConfig> getConfig() {
        return instance.mixTypes;
    }
}
