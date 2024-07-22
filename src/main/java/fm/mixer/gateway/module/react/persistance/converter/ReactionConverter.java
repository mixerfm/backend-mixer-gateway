package fm.mixer.gateway.module.react.persistance.converter;

import fm.mixer.gateway.module.react.persistance.entity.model.ReactionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReactionConverter implements AttributeConverter<ReactionType, Character> {

    @Override
    public Character convertToDatabaseColumn(ReactionType reactionType) {
        return reactionType.getCode();
    }

    @Override
    public ReactionType convertToEntityAttribute(Character character) {
        return ReactionType.fromCode(character);
    }
}
