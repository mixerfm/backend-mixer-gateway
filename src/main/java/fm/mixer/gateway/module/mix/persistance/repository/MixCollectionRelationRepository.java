package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixCollectionRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MixCollectionRelationRepository extends JpaRepository<MixCollectionRelation, Long> {

    Page<MixCollectionRelation> findByCollectionIdOrderByPosition(Long collectionId, Pageable pageable);
}
