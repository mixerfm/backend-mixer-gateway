package fm.mixer.gateway.module.user.persistance.repository;

import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used only when fetching from JWT
    Optional<User> findByEmailAndActiveIsTrue(String email);

    // Should be used everywhere
    Optional<User> findByIdentifierAndActiveIsTrue(String identifier);

    // Should be used when all info from user is required
    @Query("from User u join fetch u.address join fetch u.socialNetworks where u.identifier = :identifier and u.active is true")
    Optional<User> findActiveByIdentifier(String identifier);

    // Used in validations:

    boolean existsByEmailAndActiveIsTrueOrIdentifierAndActiveIsTrue(String identifier, String email);

    boolean existsByIdentifierAndActiveIsTrue(String identifier);

    boolean existsByEmailAndActiveIsTrue(String email);
}
