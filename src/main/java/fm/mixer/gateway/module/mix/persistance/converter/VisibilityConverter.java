package fm.mixer.gateway.module.mix.persistance.converter;

import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VisibilityConverter implements AttributeConverter<VisibilityType, Short> {

    @Override
    public Short convertToDatabaseColumn(VisibilityType visibility) {
        return visibility.getCode();
    }

    @Override
    public VisibilityType convertToEntityAttribute(Short code) {
        return VisibilityType.fromCode(code);
    }
}
