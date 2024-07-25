package fm.mixer.gateway.module.search.persistance.repository;

import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface SearchArtistRepository extends JpaRepository<UserArtist, Long> {

    Page<UserArtist> findAllByNameContainsIgnoreCase(String query, Pageable pageable);

    default Page<UserArtist> search(String query, Pageable pageable) {
        if (StringUtils.hasText(query)) {
            return findAllByNameContainsIgnoreCase(query, pageable);
        }

        return findAll(pageable);
    }
}
