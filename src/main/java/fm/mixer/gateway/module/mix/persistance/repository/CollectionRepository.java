package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public interface CollectionRepository extends JpaRepository<MixCollection, Long> {

    // TODO remove this entity graph if mixCount = 0, and rewrite MixMapper mapping for mixes
    @EntityGraph(attributePaths = {"mixes", "user", "tags", "mixes.tracks", "mixes.user", "mixes.tags", "mixes.likes"})
    Page<MixCollection> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"mixes", "user", "tags", "mixes.tracks", "mixes.user", "mixes.tags", "mixes.likes"})
    MixCollection findByIdentifier(String identifier);

    @EntityGraph(attributePaths = {"mixes", "user", "tags", "mixes.tracks", "mixes.user", "mixes.tags", "mixes.likes"})
    MixCollection findByIdentifierAndMixesTagsNameIn(String identifier, List<String> mixesTags);

    default MixCollection findFilteredByIdentifier(String identifier, List<String> mixesTags) {
        if (mixesTags.isEmpty()) {
            return findByIdentifier(identifier);
        }

        final var mixCollection = findByIdentifierAndMixesTagsNameIn(identifier, mixesTags);

        // TODO current hack for filtering mixes
        if (Objects.isNull(mixCollection)) {
            final var filteredMixes = findByIdentifier(identifier);

            filteredMixes.setMixes(Set.of());

            return filteredMixes;
        }

        return mixCollection;
    }
}
