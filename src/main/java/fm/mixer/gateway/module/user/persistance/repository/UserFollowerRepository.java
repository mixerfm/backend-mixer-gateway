package fm.mixer.gateway.module.user.persistance.repository;

import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserFollower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowerRepository extends JpaRepository<UserFollower, Long> {

    List<UserFollower> findByUser(User user);

    Page<UserFollower> findByUserIdentifier(String identifier, Pageable pageable);

    Page<UserFollower> findByFollowsUserIdentifier(String identifier, Pageable pageable);

    Optional<UserFollower> findByUserAndFollowsUser(User user, User followsUser);

    void deleteByUserOrFollowsUser(User user, User followsUser);

    default void deleteUser(User user) {
        deleteByUserOrFollowsUser(user, user);
    }
}
