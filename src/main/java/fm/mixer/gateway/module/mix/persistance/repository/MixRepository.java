package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MixRepository extends JpaRepository<Mix, Long> {

    @EntityGraph(attributePaths = {"tracks", "user", "tags", "likes", "artists"})
    Optional<Mix> findByIdentifier(String identifier);

    @EntityGraph(attributePaths = {"tracks", "user", "tags", "likes", "artists"})
    Page<Mix> findAllByUser(User user, Pageable pageable);
}
