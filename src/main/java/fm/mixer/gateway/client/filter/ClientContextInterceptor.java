package fm.mixer.gateway.client.filter;

import fm.mixer.gateway.client.holder.ClientContextHolder;
import fm.mixer.gateway.client.model.ClientContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ClientContextInterceptor implements HandlerInterceptor {

    private static final String DEVICE_ID_HEADER = "X-Device-Id";
    private static final String CLIENT_COUNTRY_HEADER = "CF-IPCountry";

    private final ClientAccessFilter clientAccessFilter;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        ClientContextHolder.set(new ClientContext(
            request.getHeader(DEVICE_ID_HEADER),
            request.getHeader(CLIENT_COUNTRY_HEADER)
        ));

        clientAccessFilter.checkClientAccess(request);

        return true;
    }
}
