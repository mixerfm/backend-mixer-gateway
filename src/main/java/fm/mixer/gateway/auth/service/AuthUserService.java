package fm.mixer.gateway.auth.service;

import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.Optional;

public abstract class AuthUserService {

    private static final String EMAIL_CLAIM = "email";

    public abstract Optional<User> getCurrentActiveUser();

    public Optional<String> resolveUserEmailFromJwt() {
        final var principal = Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getPrincipal);

        if (principal.isPresent() && principal.get() instanceof Jwt jwtPrincipal && StringUtils.hasText(jwtPrincipal.getClaimAsString(EMAIL_CLAIM))) {
            return Optional.of(jwtPrincipal.getClaimAsString(EMAIL_CLAIM));
        }

        return Optional.empty();
    }
}
