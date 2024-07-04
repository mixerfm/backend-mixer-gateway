package fm.mixer.gateway.client.holder;

import fm.mixer.gateway.client.model.ClientContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

public class ClientContextHolder {

    public static final String DEVICE_ID = "CONTEXT_DEVICE_ID";

    public static void set(ClientContext clientContext) {
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .ifPresent(attributes -> attributes.setAttribute(DEVICE_ID, clientContext.deviceId(), RequestAttributes.SCOPE_REQUEST));
    }

    public static Optional<ClientContext> get() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .map(attributes -> attributes.getAttribute(DEVICE_ID, RequestAttributes.SCOPE_REQUEST))
            .map(String.class::cast)
            .map(ClientContext::new);
    }
}
