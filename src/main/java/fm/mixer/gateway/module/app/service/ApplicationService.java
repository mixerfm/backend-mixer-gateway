package fm.mixer.gateway.module.app.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.client.holder.ClientContextHolder;
import fm.mixer.gateway.module.app.api.v1.model.FeatureList;
import fm.mixer.gateway.module.app.api.v1.model.FeatureType;
import fm.mixer.gateway.module.app.api.v1.model.VersionList;
import fm.mixer.gateway.module.app.config.AvailabilityConfig;
import fm.mixer.gateway.module.app.config.VersionsConfig;
import fm.mixer.gateway.module.app.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationMapper mapper;
    private final VersionsConfig versionConfig;
    private final AvailabilityConfig availabilityConfig;

    public VersionList getVersionList() {
        return mapper.toVersionList(versionConfig);
    }

    public FeatureList getAvailability() {
        return mapper.toFeatureList(checkAvailability());
    }

    private Map<FeatureType, Boolean> checkAvailability() {
        final var clientContext = ClientContextHolder.get().orElseThrow(AccessForbiddenException::new);
        log.info("Current client context: {}", clientContext);

        return Map.of(
            FeatureType.APPLICATION, true //availabilityConfig.getSupportedCountries().contains(clientContext.countryCode())
        );
    }
}
