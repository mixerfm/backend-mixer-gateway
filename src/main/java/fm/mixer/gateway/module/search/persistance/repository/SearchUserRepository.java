package fm.mixer.gateway.module.search.persistance.repository;

import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface SearchUserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByNameContainsIgnoreCaseAndActiveIsTrue(String query, Pageable pageable);

    Page<User> findAllByActiveIsTrue(Pageable pageable);

    default Page<User> search(String query, Pageable pageable) {
        if (StringUtils.hasText(query)) {
            return findAllByNameContainsIgnoreCaseAndActiveIsTrue(query, pageable);
        }

        return findAllByActiveIsTrue(pageable);
    }
}
