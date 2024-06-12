package fm.mixer.gateway.module.user.persistance.repository;

import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndActiveIsTrue(Long id);

    Optional<User> findByEmailAndActiveIsTrue(String email);

    Optional<User> findByIdentifierAndActiveIsTrue(String identifier);

    @EntityGraph(attributePaths = {"socialNetworks"})
    Optional<User> findByActiveIsTrueAndIdentifier(String identifier);

    boolean existsByEmailAndActiveIsTrueOrIdentifierAndActiveIsTrue(String identifier, String email);

    boolean existsByIdentifierAndActiveIsTrue(String identifier);

    boolean existsByEmailAndActiveIsTrue(String email);
}
