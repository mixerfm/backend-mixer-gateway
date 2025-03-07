package fm.mixer.gateway.module.app.api;

import fm.mixer.gateway.module.app.api.v1.ApplicationApiDelegate;
import fm.mixer.gateway.module.app.api.v1.model.FeatureList;
import fm.mixer.gateway.module.app.api.v1.model.VersionList;
import fm.mixer.gateway.module.app.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApiDelegate {

    private final ApplicationService service;

    @Override
    public ResponseEntity<VersionList> getVersionList() {
        return ResponseEntity.ok(service.getVersionList());
    }

    @Override
    public ResponseEntity<FeatureList> getAvailability() {
        return ResponseEntity.ok(service.getAvailability());
    }
}
