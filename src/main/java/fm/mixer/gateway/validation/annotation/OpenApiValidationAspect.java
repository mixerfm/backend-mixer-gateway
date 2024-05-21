package fm.mixer.gateway.validation.annotation;

import fm.mixer.gateway.validation.service.ValidateOpenApiService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class OpenApiValidationAspect {

    private final ValidateOpenApiService validateOpenApiService;

    @AfterReturning(value = "@annotation(fm.mixer.gateway.validation.annotation.OpenApiValidation)", returning = "openApiResponseEntity")
    public void validateOpenApiResponse(ResponseEntity<?> openApiResponseEntity) {
        validateOpenApiService.validateOpenApiResponse(openApiResponseEntity);
    }
}
