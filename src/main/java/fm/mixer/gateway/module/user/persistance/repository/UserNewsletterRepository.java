package fm.mixer.gateway.module.user.persistance.repository;

import fm.mixer.gateway.module.user.persistance.entity.UserNewsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNewsletterRepository extends JpaRepository<UserNewsletter, Long> {

    Optional<UserNewsletter> findByEmail(String email);

    Optional<UserNewsletter> findByIdentifierAndEmail(String token, String email);
}
