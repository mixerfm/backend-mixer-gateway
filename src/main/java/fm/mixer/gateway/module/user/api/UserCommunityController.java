package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.module.user.api.v1.UserCommunityApiDelegate;
import fm.mixer.gateway.module.user.api.v1.model.GetUserList;
import fm.mixer.gateway.module.user.service.UserCommunityService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserCommunityController implements UserCommunityApiDelegate {

    private final UserCommunityService service;

    @Override
    public ResponseEntity<GetUserList> getFollowerList(String username, List<@Pattern(regexp = "^(date|name|popularity|trend)(:(asc|desc))?$") String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getFollowerList(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<GetUserList> getFollowingList(String username, List<@Pattern(regexp = "^(date|name|popularity|trend)(:(asc|desc))?$") String> sort, Integer limit, Integer page) {
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
    public ResponseEntity<Void> reportUser(String username) {
        service.reportUser(username);

        return ResponseEntity.noContent().build();
    }
}
