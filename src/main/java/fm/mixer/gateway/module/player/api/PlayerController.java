package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.module.player.api.v1.PlayerApiDelegate;
import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.module.player.api.v1.model.VolumeValue;
import fm.mixer.gateway.module.player.service.PlayerService;
import fm.mixer.gateway.validation.annotation.OpenApiValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlayerController implements PlayerApiDelegate {

    private final PlayerService service;

    @Override
    @OpenApiValidation
    public ResponseEntity<Track> playTrack(String mixId) {
        return ResponseEntity.ok(service.playTrack(mixId));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> pauseTrack(String mixId) {
        service.pauseTrack(mixId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Track> nextTrack(String mixId) {
        return ResponseEntity.ok(service.nextTrack(mixId, false));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Track> skipTrack(String mixId) {
        return ResponseEntity.ok(service.skipTrack(mixId));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> changeTrackVolume(String mixId, VolumeValue volumeValue) {
        service.changeVolume(mixId, volumeValue);

        return ResponseEntity.noContent().build();
    }
}
