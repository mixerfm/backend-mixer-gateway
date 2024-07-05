package fm.mixer.gateway.client.holder;

import fm.mixer.gateway.client.model.ClientContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

public class ClientContextHolder {

    public static final String CLIENT_CONTEXT = "CLIENT_CONTEXT";

    public static void set(ClientContext clientContext) {
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .ifPresent(attributes -> attributes.setAttribute(CLIENT_CONTEXT, clientContext, RequestAttributes.SCOPE_REQUEST));
    }

    public static Optional<ClientContext> get() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .map(attributes -> attributes.getAttribute(CLIENT_CONTEXT, RequestAttributes.SCOPE_REQUEST))
            .map(ClientContext.class::cast);
    }
}
