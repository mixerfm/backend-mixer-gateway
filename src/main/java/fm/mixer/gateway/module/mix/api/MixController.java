package fm.mixer.gateway.module.mix.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.mix.api.v1.MixesApiDelegate;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.service.MixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MixController implements MixesApiDelegate {

    private final MixService service;

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
    public ResponseEntity<List<UserReaction>> react(String mixId, UserReaction userReaction) {
        return ResponseEntity.ok(service.react(mixId, userReaction.getType()));
    }

    @Override
    public ResponseEntity<List<UserReaction>> removeReaction(String mixId) {
        return ResponseEntity.ok(service.removeReaction(mixId));
    }
}
