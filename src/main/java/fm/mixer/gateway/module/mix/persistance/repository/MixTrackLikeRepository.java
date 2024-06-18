package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixTrack;
import fm.mixer.gateway.module.mix.persistance.entity.MixTrackLike;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MixTrackLikeRepository extends JpaRepository<MixTrackLike, Long> {

    Optional<MixTrackLike> findByUserAndTrack(User user, MixTrack track);
}
