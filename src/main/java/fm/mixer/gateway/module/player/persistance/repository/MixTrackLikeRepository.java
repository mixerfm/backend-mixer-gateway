package fm.mixer.gateway.module.player.persistance.repository;

import fm.mixer.gateway.module.player.persistance.entity.MixTrack;
import fm.mixer.gateway.module.player.persistance.entity.MixTrackLike;
import fm.mixer.gateway.module.react.persistance.repository.ReactionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MixTrackLikeRepository extends ReactionRepository<MixTrack, MixTrackLike> {
}
