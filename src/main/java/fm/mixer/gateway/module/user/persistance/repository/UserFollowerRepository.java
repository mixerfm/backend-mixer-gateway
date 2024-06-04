package fm.mixer.gateway.module.user.persistance.repository;

import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFollowerRepository extends JpaRepository<UserFollower, Long> {

    List<UserFollower> findByUser(User user);
}
