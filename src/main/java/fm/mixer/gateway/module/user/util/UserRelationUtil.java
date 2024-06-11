package fm.mixer.gateway.module.user.util;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.module.user.api.v1.model.CompactUser;
import fm.mixer.gateway.module.user.api.v1.model.UserRelation;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.repository.UserFollowerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRelationUtil {

    private static UserFollowerRepository followerRepository;

    public UserRelationUtil(UserFollowerRepository followerRepository) {
        UserRelationUtil.followerRepository = followerRepository;
    }

    public static UserRelation resolveRelation(User user) {
        final var activeUser = UserPrincipalUtil.getCurrentActiveUser();

        if (activeUser.isEmpty()) {
            return null;
        }
        if (activeUser.get().getId().equals(user.getId())) {
            return UserRelation.SELF;
        }
        if (followerRepository.findByUser(activeUser.get()).stream().anyMatch(follows -> follows.getFollowsUser().getId().equals(user.getId()))) {
            return UserRelation.FOLLOWING;
        }

        return null;
    }

    public static void resolveRelation(List<CompactUser> userList) {
        final var activeUser = UserPrincipalUtil.getCurrentActiveUser();

        if (activeUser.isEmpty()) {
            return;
        }

        final var userFollowsThem = followerRepository.findByUser(activeUser.get());

        userList.forEach(user -> {
            if (activeUser.get().getIdentifier().equals(user.getUsername())) {
                user.setRelation(UserRelation.SELF);
            }
            else if (userFollowsThem.stream().anyMatch(follows -> follows.getFollowsUser().getIdentifier().equals(user.getUsername()))) {
                user.setRelation(UserRelation.FOLLOWING);
            }
        });
    }
}
