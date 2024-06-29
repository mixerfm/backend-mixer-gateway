package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.module.player.api.v1.TrackApiDelegate;
import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.module.player.api.v1.model.UserReaction;
import fm.mixer.gateway.module.player.service.PlayerService;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.service.ReportService;
import fm.mixer.gateway.validation.annotation.OpenApiValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrackController implements TrackApiDelegate {

    private final PlayerService service;
    private final ReportService reportService;

    @Override
    @OpenApiValidation
    public ResponseEntity<TrackList> getTrackList(String mixId) {
        return ResponseEntity.ok(service.getTrackList(mixId));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> react(String trackId, UserReaction userReaction) {
        if (UserReaction.TypeEnum.REPORT.equals(userReaction.getType())) {
            reportService.report(trackId, ResourceType.TRACK);
        }
        else {
            checkReactionType(userReaction.getType());
            service.react(trackId, userReaction.getType());
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> removeReaction(String trackId, UserReaction userReaction) {
        service.removeReaction(trackId, userReaction.getType());

        return ResponseEntity.noContent().build();
    }

    private void checkReactionType(UserReaction.TypeEnum type) {
        if (!List.of(
            UserReaction.TypeEnum.LIKE,
            UserReaction.TypeEnum.DISLIKE,
            UserReaction.TypeEnum.RECOMMEND,
            UserReaction.TypeEnum.DO_NOT_RECOMMEND
        ).contains(type)) {
            throw new BadRequestException("reaction.type.not.supported.error");
        }
    }
}
