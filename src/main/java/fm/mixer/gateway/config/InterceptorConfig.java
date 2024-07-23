package fm.mixer.gateway.config;

import fm.mixer.gateway.client.filter.ClientContextInterceptor;
import fm.mixer.gateway.validation.filter.OpenApiValidateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final OpenApiValidateInterceptor openApiValidateInterceptor;
    private final ClientContextInterceptor clientContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(openApiValidateInterceptor);
        registry.addInterceptor(clientContextInterceptor);
    }
}
