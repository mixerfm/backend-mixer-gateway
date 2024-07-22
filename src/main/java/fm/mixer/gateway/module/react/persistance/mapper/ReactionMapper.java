package fm.mixer.gateway.module.react.persistance.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.InternalServerErrorException;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.persistance.entity.ReactionContainerEntity;
import fm.mixer.gateway.module.react.persistance.entity.ReactionEntity;
import fm.mixer.gateway.module.react.persistance.entity.model.ReactionType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public abstract class ReactionMapper {

    public static <ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> List<UserReaction> toReactions(final Set<TABLE> reactions, final User user) {
        return reactions.stream()
            .filter(reaction -> user.getId().equals(reaction.getUser().getId()))
            .map(reaction -> toUserReactionType(reaction.getType(), reaction.getValue()))
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
    public static <ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> TABLE toReactionEntity(Class<TABLE> tableClass, User user, ReactionContainerEntity<ITEM, TABLE> item, UserReaction.TypeEnum type) {
        final var entity = getTableClass(tableClass);

        entity.setUser(user);
        entity.setItem((ITEM) item);
        entity.setType(toReactionType(type));
        entity.setValue(toReactionValue(type));

        return entity;
    }

    public static ReactionType toReactionType(UserReaction.TypeEnum type) {
        // Used when removing the reaction without specifying which one - default one
        if (Objects.isNull(type)) {
            return ReactionType.LIKE;
        }

        return switch (type) {
            case LIKE, DISLIKE -> ReactionType.LIKE;
            case RECOMMEND, DO_NOT_RECOMMEND -> ReactionType.RECOMMEND;
            default -> throw new InternalServerErrorException("Unsupported reaction type: " + type);
        };
    }

    public static boolean toReactionValue(UserReaction.TypeEnum type) {
        return switch (type) {
            case LIKE, RECOMMEND -> true;
            case DISLIKE, DO_NOT_RECOMMEND -> false;
            default -> throw new InternalServerErrorException("Unsupported reaction type: " + type);
        };
    }

    private static UserReaction.TypeEnum toUserReactionType(final ReactionType type, final Boolean value) {
        return switch (type) {
            case LIKE -> Boolean.TRUE.equals(value) ? UserReaction.TypeEnum.LIKE : UserReaction.TypeEnum.DISLIKE;
            case RECOMMEND -> Boolean.TRUE.equals(value) ? UserReaction.TypeEnum.RECOMMEND : UserReaction.TypeEnum.DO_NOT_RECOMMEND;
        };
    }

    public static List<UserReaction.TypeEnum> toAllowedUserReactions(ResourceType resourceType) {
        return switch (resourceType) {
            case COMMENT, COLLECTIONS, MIX -> List.of(UserReaction.TypeEnum.LIKE, UserReaction.TypeEnum.DISLIKE);
            case TRACK -> List.of(
                    UserReaction.TypeEnum.LIKE,
                    UserReaction.TypeEnum.DISLIKE,
                    UserReaction.TypeEnum.RECOMMEND,
                    UserReaction.TypeEnum.DO_NOT_RECOMMEND
            );
            default -> List.of();
        };
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
