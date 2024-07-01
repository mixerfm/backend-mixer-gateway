package fm.mixer.gateway.module.mix.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.module.mix.api.v1.MixesApiDelegate;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserReaction;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.service.MixService;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MixController implements MixesApiDelegate {

    private final MixService service;
    private final ReportService reportService;

    @Override
    public ResponseEntity<SingleMix> getSingleMix(String mixId) {
        return ResponseEntity.ok(service.getSingleMix(mixId));
    }

    @Override
    public ResponseEntity<UserLikedMixes> getUserLikedMixes(String username, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getUserLikedMixes(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<UserListenedMixes> getUserMixesHistory(String username, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getUserMixesHistory(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<UserUploadedMixes> getUserUploadedMixes(String username, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getUserUploadedMixes(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<Void> react(String mixId, UserReaction userReaction) {
        if (UserReaction.TypeEnum.REPORT.equals(userReaction.getType())) {
            reportService.report(mixId, ResourceType.MIX);
        }
        else {
            checkReactionType(userReaction.getType());
            service.react(mixId, UserReaction.TypeEnum.LIKE.equals(userReaction.getType()));
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> removeReaction(String mixId) {
        service.removeReaction(mixId);

        return ResponseEntity.noContent().build();
    }

    private void checkReactionType(UserReaction.TypeEnum type) {
        if (!List.of(UserReaction.TypeEnum.LIKE, UserReaction.TypeEnum.DISLIKE).contains(type)) {
            throw new BadRequestException("reaction.type.not.supported.error");
        }
    }
}
