package fm.mixer.gateway.validation.filter;

import fm.mixer.gateway.validation.service.ValidateOpenApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiValidateInterceptor implements HandlerInterceptor {

    private final ValidateOpenApiService validateOpenApiService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        validateOpenApiService.validateOpenApiRequest();

        return true;
    }
}
