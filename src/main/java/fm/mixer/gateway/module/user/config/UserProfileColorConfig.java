package fm.mixer.gateway.module.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties("mixer-gateway.user-colors")
public class UserProfileColorConfig {

    private String inactive;
    private List<String> active;
}
