package fm.mixer.gateway.module.player.persistance.repository;

import fm.mixer.gateway.module.player.persistance.entity.MixTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MixTrackRepository extends JpaRepository<MixTrack, Long> {

    Optional<MixTrack> findByIdentifier(String identifier);
}
