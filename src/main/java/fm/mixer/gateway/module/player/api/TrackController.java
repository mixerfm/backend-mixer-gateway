package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.player.api.v1.TrackApiDelegate;
import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.module.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrackController implements TrackApiDelegate {

    private final PlayerService service;

    @Override
    public ResponseEntity<TrackList> getTrackList(String mixId) {
        return ResponseEntity.ok(service.getTrackList(mixId));
    }

    @Override
    public ResponseEntity<List<UserReaction>> react(String trackId, UserReaction userReaction) {
        return ResponseEntity
            .created(URI.create(String.format("/tracks/%s/reactions", trackId)))
            .body(service.react(trackId, userReaction.getType()));
    }

    @Override
    public ResponseEntity<List<UserReaction>> removeReaction(String trackId, UserReaction userReaction) {
        return ResponseEntity.ok(service.removeReaction(trackId, userReaction.getType()));
    }
}
