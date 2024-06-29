package fm.mixer.gateway.common.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "username", source = "identifier")
@Mapping(target = "displayName", source = "name")
@Mapping(target = "avatarUrl", source = "avatar")
@Mapping(target = "active", source = "active", defaultValue = "true")
public @interface CreatorCommonMapping {
}
