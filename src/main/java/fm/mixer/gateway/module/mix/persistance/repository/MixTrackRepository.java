package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MixTrackRepository extends JpaRepository<MixTrack, Long> {
}
