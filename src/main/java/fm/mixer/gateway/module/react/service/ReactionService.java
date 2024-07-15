package fm.mixer.gateway.module.react.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.react.persistance.entity.ReactionContainerEntity;
import fm.mixer.gateway.module.react.persistance.entity.ReactionEntity;
import fm.mixer.gateway.module.react.persistance.mapper.ReactionMapper;
import fm.mixer.gateway.module.react.persistance.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReactionService<ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> {

    private final ReactionRepository<ITEM, TABLE> repository;

    public List<UserReaction> react(final ReactionContainerEntity<ITEM, TABLE> item, final boolean like) {
        final var user = UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);

        // Find old record and change "liked" flag. If there is no old record create new object with mapper and store it.
        final var reaction = repository.save(
            repository.findByUserAndItem(user, item).map(likeRecord -> {
                likeRecord.setLiked(like);
                return likeRecord;
            }).orElse(ReactionMapper.toReactionEntity(repository.getEntityClass() , user, item, like))
        );

        item.getReactions().remove(reaction);
        item.getReactions().add(reaction);

        return ReactionMapper.toReactions(item.getReactions(), user);
    }

    public List<UserReaction> removeReaction(final ReactionContainerEntity<ITEM, TABLE> item) {
        final var user = UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);
        final var reaction = repository.findByUserAndItem(user, item).orElseThrow(ResourceNotFoundException::new);

        item.getReactions().remove(reaction);
        repository.delete(reaction);

        return ReactionMapper.toReactions(item.getReactions(), user);
    }
}
