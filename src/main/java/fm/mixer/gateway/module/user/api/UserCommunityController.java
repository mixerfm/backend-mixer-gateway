package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.service.ReportService;
import fm.mixer.gateway.module.user.api.v1.UserCommunityApiDelegate;
import fm.mixer.gateway.module.user.api.v1.model.GetUserList;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.user.service.UserCommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserCommunityController implements UserCommunityApiDelegate {

    private final UserCommunityService service;
    private final ReportService reportService;

    @Override
    public ResponseEntity<GetUserList> getFollowerList(String username, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getFollowerList(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<GetUserList> getFollowingList(String username, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getFollowingList(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<Void> follow(String username) {
        service.follow(username);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> unfollow(String username) {
        service.unfollow(username);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> removeFollower(String username) {
        service.removeFollower(username);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserReaction>> react(String username, UserReaction userReaction) {
        if (!UserReaction.TypeEnum.REPORT.equals(userReaction.getType())) {
            throw new BadRequestException("reaction.type.not.supported.error");
        }

        reportService.report(username, ResourceType.USER);

        return ResponseEntity
            .created(URI.create(String.format("/users/%s/reactions", username)))
            .body(List.of());
    }
}
