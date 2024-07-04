package fm.mixer.gateway.client.filter;

import fm.mixer.gateway.client.holder.ClientContextHolder;
import fm.mixer.gateway.client.model.ClientContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ClientContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        final var deviceId = request.getHeader("X-Device-Id");

        if (StringUtils.hasText(deviceId)) {
            ClientContextHolder.set(new ClientContext(deviceId));
        }

        filterChain.doFilter(request, response);
    }
}
