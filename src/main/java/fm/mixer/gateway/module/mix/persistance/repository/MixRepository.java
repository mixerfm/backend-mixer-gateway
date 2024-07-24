package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MixRepository extends JpaRepository<Mix, Long> {

    Optional<Mix> findByIdentifier(String identifier);

    @Query("from Mix m join fetch m.tracks where m.identifier = :identifier")
    Optional<Mix> findByIdentifierWithTracks(String identifier);

    Page<Mix> findAllByUser(User user, Pageable pageable);

    Page<Mix> findAllByTagsNameInOrArtistsIdentifierIn(List<String> tags, List<String> artists, Pageable pageable);

    default Page<Mix> search(List<String> filter, Pageable pageable) {
        if (!filter.isEmpty()) {
            return findAllByTagsNameInOrArtistsIdentifierIn(filter, filter, pageable);
        }

        return findAll(pageable);
    }
}
