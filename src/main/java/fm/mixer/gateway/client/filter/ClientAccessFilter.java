package fm.mixer.gateway.client.filter;

import fm.mixer.gateway.client.holder.ClientContextHolder;
import fm.mixer.gateway.error.exception.UnsupportedClientLocationException;
import fm.mixer.gateway.module.app.config.AvailabilityConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAccessFilter {

    private final AvailabilityConfig config;

    public void checkClientAccess(HttpServletRequest request) {
        if (isExcluded(request) || isSupportedCountry()) {
            return;
        }

        throw new UnsupportedClientLocationException();
    }

    private boolean isExcluded(HttpServletRequest request) {
        return config.getExcludedEndpoints().stream()
            .anyMatch(endpoint -> new RegexRequestMatcher(endpoint, HttpMethod.GET.name()).matches(request));
    }

    private boolean isSupportedCountry() {
        return ClientContextHolder.get()
            .map(context -> config.getSupportedCountries().contains(context.countryCode()))
            .orElse(false);
    }
}
