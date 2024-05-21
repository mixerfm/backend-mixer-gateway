package fm.mixer.gateway.common.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "metadata", expression = "java(PaginationMapper.toPaginationMetadata(items, paginationRequest))")
public @interface PaginatedMapping {
}
