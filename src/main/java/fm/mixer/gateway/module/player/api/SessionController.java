package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.module.player.api.v1.SessionApiDelegate;
import fm.mixer.gateway.module.player.api.v1.model.Session;
import fm.mixer.gateway.module.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SessionController implements SessionApiDelegate {

    private final PlayerService service;

    @Override
    public ResponseEntity<Session> getPlayerSession() {
        return ResponseEntity.ok(service.getPlayerSession());
    }
}
