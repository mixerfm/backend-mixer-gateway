package fm.mixer.gateway.module.user.persistance.entity.converter;

import fm.mixer.gateway.module.user.persistance.entity.model.UserSubscriptionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SubscriptionConverter implements AttributeConverter<UserSubscriptionType, Short> {

    @Override
    public Short convertToDatabaseColumn(UserSubscriptionType userSubscriptionType) {
        return userSubscriptionType.getCode();
    }

    @Override
    public UserSubscriptionType convertToEntityAttribute(Short code) {
        return UserSubscriptionType.fromCode(code);
    }
}
