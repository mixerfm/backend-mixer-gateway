package fm.mixer.gateway.auth.service;

import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mixer-gateway.security", name = "enabled", havingValue = "false")
class LocalAuthService extends AuthUserService {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";

    private final HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentActiveUser() {
        // For local development without JWT token
        final var userId = httpServletRequest.getHeader(USER_ID_HEADER);
        if (StringUtils.hasText(userId)) {
            return userRepository.findByIdentifierAndActiveIsTrue(userId);
        }

        return resolveUserEmailFromJwt().flatMap(userRepository::findByEmailAndActiveIsTrue);
    }

    @Override
    public Optional<String> resolveUserEmailFromJwt() {
        // For local development without JWT token
        final var userEmail = httpServletRequest.getHeader(USER_EMAIL_HEADER);
        if (StringUtils.hasText(userEmail)) {
            return Optional.of(userEmail);
        }

        return super.resolveUserEmailFromJwt();
    }
}
