package fm.mixer.gateway.module.react.persistance.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.InternalServerErrorException;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.react.persistance.entity.ReactionContainerEntity;
import fm.mixer.gateway.module.react.persistance.entity.ReactionEntity;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public abstract class ReactionMapper {

    public static <ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> List<UserReaction> toReactions(final Set<TABLE> likes, final User user) {
        return likes.stream()
            .filter(like -> user.getId().equals(like.getUser().getId()))
            .map(like -> like.getLiked() ? UserReaction.TypeEnum.LIKE : UserReaction.TypeEnum.DISLIKE)
            .map(UserReaction::new)
            .toList();
    }

    public static <ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> List<UserReaction> toReactions(final Set<TABLE> reactions) {
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();

        if (currentActiveUser.isEmpty()) {
            return List.of();
        }

        return toReactions(reactions, currentActiveUser.get());
    }

    @SuppressWarnings("unchecked")
    public static <ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> TABLE toReactionEntity(Class<TABLE> tableClass, User user, ReactionContainerEntity<ITEM, TABLE> item, Boolean liked) {
        final var entity = getTableClass(tableClass);

        entity.setUser(user);
        entity.setItem((ITEM) item);
        entity.setLiked(liked);

        return entity;
    }

    private static <ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> TABLE getTableClass(Class<TABLE> tableClass) {
        try {
            return tableClass.cast(Arrays.stream(tableClass.getDeclaredConstructors()).findFirst().orElseThrow().newInstance());
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
