package fm.mixer.gateway.auth.service;

import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mixer-gateway.security", name = "enabled", havingValue = "true", matchIfMissing = true)
class RemoteAuthService extends AuthUserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentActiveUser() {
        return resolveUserEmailFromJwt().flatMap(userRepository::findByEmailAndActiveIsTrue);
    }
}
