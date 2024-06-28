package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<MixCollection, Long> {

    Optional<MixCollection> findByIdentifier(String identifier);
}
