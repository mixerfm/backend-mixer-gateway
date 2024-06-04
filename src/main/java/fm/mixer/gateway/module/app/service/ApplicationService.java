package fm.mixer.gateway.module.app.service;

import fm.mixer.gateway.module.app.api.v1.model.VersionList;
import fm.mixer.gateway.module.app.config.VersionsConfig;
import fm.mixer.gateway.module.app.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final VersionsConfig config;
    private final ApplicationMapper mapper;

    public VersionList getVersionList() {
        return mapper.toVersionList(config);
    }
}
