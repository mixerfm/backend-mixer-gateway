package fm.mixer.gateway.common.filter;

import fm.mixer.gateway.common.filter.model.CachedBodyHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Required so other filters can use request body
 */
@Component
@WebFilter(filterName = "content-caching-filter", urlPatterns = "/*")
public class ContentCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new CachedBodyHttpServletRequest(request), response);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        // request wrapper doesn't work properly for form encoded requests.
        return MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType());
    }
}
