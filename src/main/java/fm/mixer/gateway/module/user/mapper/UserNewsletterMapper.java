package fm.mixer.gateway.module.user.mapper;

import fm.mixer.gateway.module.user.persistance.entity.UserNewsletter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(imports = {UUID.class})
public interface UserNewsletterMapper {

    @Mapping(target = "identifier", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "id", ignore = true)
    UserNewsletter toUserNewsletterEntity(String email);
}
