package fm.mixer.gateway.module.user.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.user.api.v1.model.GetUserList;
import fm.mixer.gateway.module.user.mapper.UserCommunityMapper;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.repository.UserFollowerRepository;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommunityService {

    private final UserCommunityMapper mapper;
    private final UserRepository userRepository;
    private final UserFollowerRepository repository;

    public GetUserList getFollowerList(String username, PaginationRequest paginationRequest) {
        final var theyFollowsUser = repository.findByFollowsUserIdentifier(username, paginationRequest.pageable());

        return mapper.toUserFollowerList(theyFollowsUser, paginationRequest);
    }

    public GetUserList getFollowingList(String username, PaginationRequest paginationRequest) {
        final var userFollowsThem = repository.findByUserIdentifier(username, paginationRequest.pageable());

        return mapper.toUserFollowingList(userFollowsThem, paginationRequest);
    }

    public void follow(String username) {
        final var currentUser = getCurrentUser();
        final var user = userRepository.findByIdentifierAndActiveIsTrue(username).orElseThrow(ResourceNotFoundException::new);

        // User already follow this user
        if (repository.findByUserAndFollowsUser(getCurrentUser(), user).isPresent()) {
            return;
        }

        changeFollowerNumbers(currentUser, user, 1);

        repository.save(mapper.toUserFollower(currentUser, user));
    }

    public void unfollow(String username) {
        final var currentUser = getCurrentUser();
        final var user = userRepository.findByIdentifierAndActiveIsTrue(username).orElseThrow(ResourceNotFoundException::new);

        repository.findByUserAndFollowsUser(currentUser, user).ifPresent((userFollower) -> {
            changeFollowerNumbers(currentUser, user, -1);

            repository.delete(userFollower);
        });
    }

    public void removeFollower(String username) {
        final var currentUser = getCurrentUser();
        final var user = userRepository.findByIdentifierAndActiveIsTrue(username).orElseThrow(ResourceNotFoundException::new);

        repository.findByUserAndFollowsUser(user, currentUser).ifPresent((userFollower) -> {
            changeFollowerNumbers(user, currentUser, -1);

            repository.delete(userFollower);
        });
    }

    private void changeFollowerNumbers(User removeFollowing, User removeFollower, int byCount) {
        removeFollower.setNumberOfFollowers(removeFollower.getNumberOfFollowers() + byCount);
        userRepository.save(removeFollower);

        removeFollowing.setNumberOfFollowing(removeFollowing.getNumberOfFollowing() + byCount);
        userRepository.save(removeFollowing);
    }

    private User getCurrentUser() {
        return UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);
    }
}
