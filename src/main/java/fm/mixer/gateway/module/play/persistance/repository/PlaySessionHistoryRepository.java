package fm.mixer.gateway.module.play.persistance.repository;

import fm.mixer.gateway.module.play.persistance.entity.PlaySessionHistory;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaySessionHistoryRepository extends JpaRepository<PlaySessionHistory, Long> {

    @EntityGraph(attributePaths = {"user", "mix", "mix.tracks", "mix.user", "mix.tags", "mix.likes", "mix.artists"})
    Page<PlaySessionHistory> findByUser(User user, Pageable pageable);
}
