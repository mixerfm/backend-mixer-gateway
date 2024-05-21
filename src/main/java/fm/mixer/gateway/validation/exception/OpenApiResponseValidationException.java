package fm.mixer.gateway.validation.exception;

import fm.mixer.gateway.validation.exception.model.OpenApiFieldValidation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OpenApiResponseValidationException extends RuntimeException {

    private final transient List<OpenApiFieldValidation> openApiFieldValidations;
}
