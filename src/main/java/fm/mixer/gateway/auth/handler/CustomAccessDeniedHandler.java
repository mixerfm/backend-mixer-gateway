package fm.mixer.gateway.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import fm.mixer.gateway.error.service.ErrorResponseService;
import fm.mixer.gateway.model.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;
    private final ErrorResponseService service;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        final var errorResponse = service.createError(ErrorType.ACCESS_FORBIDDEN);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorResponse.getStatusCode().value());
        response.getWriter().write(mapper.writeValueAsString(errorResponse.getBody()));
    }
}
