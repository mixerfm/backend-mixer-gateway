package fm.mixer.gateway.module.react.persistance.repository;

import fm.mixer.gateway.error.exception.InternalServerErrorException;
import fm.mixer.gateway.module.react.persistance.entity.ReactionContainerEntity;
import fm.mixer.gateway.module.react.persistance.entity.ReactionEntity;
import fm.mixer.gateway.module.react.persistance.entity.model.ReactionType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

@NoRepositoryBean
public interface ReactionRepository<ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> extends JpaRepository<TABLE, Long> {

    Optional<TABLE> findByUserAndItemAndType(User user, ReactionContainerEntity<ITEM, TABLE> item, ReactionType type);

    @SuppressWarnings("unchecked")
    default Class<TABLE> getEntityClass() {
        try {
            final var reactionRepositoryClass = (Class<ReactionRepository<ITEM, TABLE>>) Arrays.stream(getClass().getGenericInterfaces())
                .findFirst().orElseThrow();

            final var parametersType = (ParameterizedType) Arrays.stream(reactionRepositoryClass.getGenericInterfaces())
                .findFirst().orElseThrow();

            return (Class<TABLE>) parametersType.getActualTypeArguments()[1];
        }
        catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
