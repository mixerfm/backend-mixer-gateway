package fm.mixer.gateway.module.search.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface SearchMixRepository extends JpaRepository<Mix, Long> {

    Page<Mix> findAllByNameContainsIgnoreCase(String query, Pageable pageable);

    default Page<Mix> search(String query, Pageable pageable) {
        if (StringUtils.hasText(query)) {
            return findAllByNameContainsIgnoreCase(query, pageable);
        }

        return findAll(pageable);
    }
}
