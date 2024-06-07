package fm.mixer.gateway.error.mapper;

import fm.mixer.gateway.model.Error;
import fm.mixer.gateway.model.ErrorType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Mapper(imports = {UUID.class})
public interface ErrorMapper {

    @ValueMapping(target = "INTERNAL_SERVER_ERROR", source = "INTERNAL_SERVER_ERROR")
    @ValueMapping(target = "INTERNAL_SERVER_ERROR", source = "EXTERNAL_SERVICE_ERROR")
    @ValueMapping(target = "SERVICE_UNAVAILABLE", source = "EXTERNAL_SERVICE_UNAVAILABLE")
    @ValueMapping(target = "UNPROCESSABLE_ENTITY", source = "UNPROCESSABLE_ENTITY")
    @ValueMapping(target = "FORBIDDEN", source = "ACCESS_FORBIDDEN")
    @ValueMapping(target = "UNAUTHORIZED", source = "UNAUTHORIZED_ACCESS")
    @ValueMapping(target = "METHOD_NOT_ALLOWED", source = "REQUEST_METHOD_NOT_SUPPORTED")
    @ValueMapping(target = "NOT_ACCEPTABLE", source = "MEDIA_TYPE_NOT_ACCEPTABLE")
    @ValueMapping(target = "UNSUPPORTED_MEDIA_TYPE", source = "MEDIA_TYPE_NOT_SUPPORTED")
    @ValueMapping(target = "TOO_MANY_REQUESTS", source = "TOO_MANY_REQUESTS")
    @ValueMapping(target = "NOT_FOUND", source = "RESOURCE_NOT_FOUND")
    @ValueMapping(target = "BAD_REQUEST", source = "BAD_REQUEST")
    @ValueMapping(target = "BAD_REQUEST", source = "METHOD_ARGUMENT_TYPE_MISMATCH")
    @ValueMapping(target = "BAD_REQUEST", source = "METHOD_ARGUMENT_NOT_VALID")
    @ValueMapping(target = "BAD_REQUEST", source = "METHOD_ARGUMENT_CONSTRAINT_VIOLATION")
    @ValueMapping(target = "BAD_REQUEST", source = "MISSING_REQUEST_PARAMETER")
    @ValueMapping(target = "BAD_REQUEST", source = "ILLEGAL_ARGUMENT")
    @ValueMapping(target = "BAD_REQUEST", source = "MISSING_REQUEST_HEADER")
    @ValueMapping(target = "BAD_REQUEST", source = "MISSING_REQUEST_PATH_VARIABLE")
    HttpStatus mapToHttpStatus(ErrorType errorResponseType);

    @Mapping(target = "identifier", expression = "java(UUID.randomUUID())")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "type", source = "type")
    Error mapToError(ErrorType type, String description);
}
