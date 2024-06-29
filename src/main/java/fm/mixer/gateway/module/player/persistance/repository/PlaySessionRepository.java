package fm.mixer.gateway.module.player.persistance.repository;

import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaySessionRepository extends JpaRepository<PlaySession, Long> {

    @Query("from PlaySession ps join fetch ps.mix where ps.user = :user and ps.mix.identifier = :mixIdentifier")
    Optional<PlaySession> findByUserAndMixIdentifier(User user, String mixIdentifier);

    @Query("from PlaySession ps where ps.user = :user order by ps.updatedAt desc limit 1")
    Optional<PlaySession> getCurrentActiveSessionForUser(User user);

    Page<PlaySession> findByUser(User user, Pageable pageable);
}
