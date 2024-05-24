package fm.mixer.gateway.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonObjectMapperConfig {

    // We are using custom format because neither DateTimeFormat.ISO_INSTANT nor ISO_ZONED/OFFSET_DATE_TIME are good formats
    public static final String LOCAL_DATE_TIME_SERIALIZATION_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        // serialize LocalDateTime to ISO8601 compliant string; force strict validation for duplicate keys
        return builder -> builder
            .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_SERIALIZATION_PATTERN)))
            .featuresToEnable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    }
}
