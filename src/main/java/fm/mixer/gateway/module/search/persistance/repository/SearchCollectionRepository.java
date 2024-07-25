package fm.mixer.gateway.module.search.persistance.repository;

import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface SearchCollectionRepository extends JpaRepository<MixCollection, Long> {

    Page<MixCollection> findAllByNameContainsIgnoreCase(String query, Pageable pageable);

    default Page<MixCollection> search(String query, Pageable pageable) {
        if (StringUtils.hasText(query)) {
            return findAllByNameContainsIgnoreCase(query, pageable);
        }

        return findAll(pageable);
    }
}
