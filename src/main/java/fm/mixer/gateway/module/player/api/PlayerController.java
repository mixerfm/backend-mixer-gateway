package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.module.player.api.v1.PlayerApiDelegate;
import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.module.player.api.v1.model.VolumeValue;
import fm.mixer.gateway.module.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlayerController implements PlayerApiDelegate {

    private final PlayerService service;

    @Override
    public ResponseEntity<TrackList> getTrackList(String mixId) {
        return ResponseEntity.ok(service.getTrackList(mixId));
    }

    @Override
    public ResponseEntity<Track> playTrack(String mixId) {
        return ResponseEntity.ok(service.playTrack(mixId));
    }

    @Override
    public ResponseEntity<Void> pauseTrack(String mixId) {
        service.pauseTrack(mixId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Track> nextTrack(String mixId) {
        return ResponseEntity.ok(service.nextTrack(mixId));
    }

    @Override
    public ResponseEntity<Track> skipTrack(String mixId) {
        return ResponseEntity.ok(service.skipTrack(mixId));
    }

    @Override
    public ResponseEntity<Void> changeTrackVolume(String mixId, VolumeValue volumeValue) {
        service.changeVolume(mixId, volumeValue);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> likeTrack(String mixId) {
        service.setLikeFlag(mixId, true);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> dislikeTrack(String mixId) {
        service.setLikeFlag(mixId, false);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> recommendTrack(String mixId) {
        service.setRecommendedFlag(mixId, true);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> doNotRecommendTrack(String mixId) {
        service.setRecommendedFlag(mixId, false);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> reportTrack(String mixId) {
        service.reportTrack(mixId);

        return ResponseEntity.noContent().build();
    }
}
