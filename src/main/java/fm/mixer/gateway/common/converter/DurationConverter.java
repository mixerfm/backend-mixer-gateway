package fm.mixer.gateway.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return duration.toSeconds();
    }

    @Override
    public Duration convertToEntityAttribute(Long value) {
        return Duration.ofSeconds(value);
    }
}
