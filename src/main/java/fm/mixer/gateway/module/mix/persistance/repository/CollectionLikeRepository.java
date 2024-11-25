package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollectionLike;
import fm.mixer.gateway.module.react.persistance.repository.ReactionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionLikeRepository extends ReactionRepository<MixCollection, MixCollectionLike> {
}
