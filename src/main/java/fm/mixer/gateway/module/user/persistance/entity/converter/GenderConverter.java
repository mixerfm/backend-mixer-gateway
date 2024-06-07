package fm.mixer.gateway.module.user.persistance.entity.converter;

import fm.mixer.gateway.module.user.persistance.entity.model.UserGender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<UserGender, Character> {

    @Override
    public Character convertToDatabaseColumn(UserGender gender) {
        return Optional.ofNullable(gender).map(UserGender::getCode).orElse(null);
    }

    @Override
    public UserGender convertToEntityAttribute(Character code) {
        return Optional.ofNullable(code).map(UserGender::fromCode).orElse(null);
    }
}
