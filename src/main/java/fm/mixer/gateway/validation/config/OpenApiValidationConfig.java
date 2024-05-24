package fm.mixer.gateway.validation.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mixer-gateway.openapi.validation")
@RequiredArgsConstructor
public class OpenApiValidationConfig {

    private static final String SPECIFICATION_PATH = "static/endpoints/%s.yaml";

    private List<Specification> specifications;

    private final Map<String, OpenApi3> openApi3Map = new HashMap<>();

    @Getter
    @Setter
    @Configuration
    public static class Specification {
        private String name;
        private Boolean request = true;
        private Boolean response = true;
        private List<String> paths;
        private List<String> versions = List.of();
    }

    @PostConstruct
    private void init() {
        final var classLoader = getClass().getClassLoader();

        specifications.stream()
            .map(Specification::getName)
            .collect(Collectors.toSet())
            .forEach(specification -> {
                try {
                    final var api = new OpenApi3Parser()
                        .parse(classLoader.getResource(String.format(SPECIFICATION_PATH, specification)), false);

                    openApi3Map.put(specification, api);
                }
                catch (ResolutionException | ValidationException e) {
                    log.error("Unable to parse OpenAPI specification: {}", e.getMessage(), e);
                }
            });
    }

    public OpenApi3 getOpenApiSpecification(String name) {
        return openApi3Map.get(name);
    }
}