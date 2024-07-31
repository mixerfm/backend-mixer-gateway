package fm.mixer.gateway.module.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Setter
@Configuration
@ConfigurationProperties("mixer-gateway.messaging")
@ConditionalOnProperty(prefix = "mixer-gateway.messaging", value = "enabled", havingValue = "true")
public class FirebaseMessagingConfiguration {

    private Resource configFile;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        return FirebaseMessaging.getInstance(
            FirebaseApp.initializeApp(
                FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(configFile.getInputStream()))
                    .build()
            )
        );
    }
}
