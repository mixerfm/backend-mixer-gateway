package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.react.persistance.entity.model.ReactionType;
import fm.mixer.gateway.module.react.persistance.repository.ReactionRepository;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface MixLikeRepository extends ReactionRepository<Mix, MixLike> {

    @EntityGraph(attributePaths = {"user", "item", "item.user", "item.tags", "item.reactions", "item.artists"})
    Page<MixLike> findByUserAndTypeAndValueIsTrue(User user, ReactionType type, Pageable pageable);
}
