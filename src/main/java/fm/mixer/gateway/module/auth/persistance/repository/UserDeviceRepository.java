package fm.mixer.gateway.module.auth.persistance.repository;

import fm.mixer.gateway.module.auth.persistance.entity.UserDevice;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    List<UserDevice> findAllByUser(User user);

    Optional<UserDevice> findByIdentifierAndUser(String identifier, User user);

    void deleteAllByUser(User user);
}
