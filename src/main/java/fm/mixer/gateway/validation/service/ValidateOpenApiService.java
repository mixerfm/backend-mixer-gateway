package fm.mixer.gateway.validation.service;

import fm.mixer.gateway.error.exception.InternalServerErrorException;
import fm.mixer.gateway.validation.config.OpenApiValidationConfig;
import fm.mixer.gateway.validation.exception.OpenApiRequestValidationException;
import fm.mixer.gateway.validation.exception.model.OpenApiFieldValidation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidateOpenApiService {

    private final HttpServletRequest httpServletRequest;
    private final OpenApiValidationConfig openApiValidationConfig;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final Map<OpenApiValidationConfig.Specification, RequestValidator> specificationValidatorMap = new ConcurrentHashMap<>();

    private static final String ACCEPT_VERSION_HEADER = "x-accept-version";

    public void validateOpenApiRequest() {
        final var specification = getSpecification();
        if (Objects.nonNull(specification) && specification.getRequest()) {
            try {
                getValidator(specification).validate(getRequest());
            }
            catch (ValidationException validationException) {
                throw new OpenApiRequestValidationException(buildOpenApiFieldValidationList(validationException));
            }
        }
    }

    private OpenApiValidationConfig.Specification getSpecification() {
        final var specificationList = openApiValidationConfig.getSpecifications().stream()
            .filter(this::isPathMatch)
            .toList();

        return specificationList.stream()
            .filter(s -> s.getVersions().stream().anyMatch(v -> v.equals(httpServletRequest.getHeader(ACCEPT_VERSION_HEADER))))
            .findFirst()
            .orElseGet(() -> specificationList.stream()
                .filter(s -> s.getVersions().isEmpty())
                .findFirst()
                .orElse(null)
            );
    }

    private boolean isPathMatch(final OpenApiValidationConfig.Specification specification) {
        return specification.getPaths().stream().anyMatch(path -> antPathMatcher.match(path, httpServletRequest.getServletPath()));
    }

    private RequestValidator getValidator(final OpenApiValidationConfig.Specification specification) {
        return specificationValidatorMap.computeIfAbsent(specification, k -> {
            final var openApi = openApiValidationConfig.getOpenApiSpecification(specification.getName());
            if (Objects.nonNull(openApi)) {
                return new RequestValidator(openApi);
            }
            else {
                log.warn("Specification not parsed: {}", k.getName());
                return null;
            }
        });
    }

    private Request getRequest() {
        try {
            // ServletRequest.of(httpServletRequest) is using old javax package, so it is not suitable for Spring Boot 3
            final var builder = new DefaultRequest.Builder(httpServletRequest.getRequestURL().toString(), Request.Method.getMethod(httpServletRequest.getMethod()));
            if (HttpMethod.GET.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
                builder.query(httpServletRequest.getQueryString());
            }
            else {
                builder.body(Body.from(httpServletRequest.getInputStream()));
            }

            final var headerNames = httpServletRequest.getHeaderNames();
            if (Objects.nonNull(headerNames)) {
                while (headerNames.hasMoreElements()) {
                    final var headerName = headerNames.nextElement();
                    final var headerValues = httpServletRequest.getHeaders(headerName);

                    while (headerValues.hasMoreElements()) {
                        builder.header(headerName, headerValues.nextElement());
                    }
                }
            }

            return builder.build();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private List<OpenApiFieldValidation> buildOpenApiFieldValidationList(final ValidationException e) {
        log.warn(e.getMessage(), e);

        return e.results().items().stream().map(item -> new OpenApiFieldValidation(item.dataCrumbs(), item.message())).toList();
    }
}
