package fm.mixer.gateway.module.react.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.module.react.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    public void report(String identifier, ResourceType resource) {
        final var user = UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);

        // TODO implement
        log.warn("Reported {} with id {}, by user {}.", resource.name(), identifier, user.getId());
    }
}
