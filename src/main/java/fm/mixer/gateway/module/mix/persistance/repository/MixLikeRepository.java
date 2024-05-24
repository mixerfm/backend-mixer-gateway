package fm.mixer.gateway.module.mix.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MixLikeRepository extends JpaRepository<MixLike, Long> {

    @EntityGraph(attributePaths = {"user", "mix", "mix.tracks", "mix.user", "mix.tags", "mix.likes"})
    Page<MixLike> findByUserAndLikedIsTrue(User user, Pageable pageable);
}
