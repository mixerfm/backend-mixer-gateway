package fm.mixer.gateway.module.react.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.persistance.entity.ReactionContainerEntity;
import fm.mixer.gateway.module.react.persistance.entity.ReactionEntity;
import fm.mixer.gateway.module.react.persistance.mapper.ReactionMapper;
import fm.mixer.gateway.module.react.persistance.repository.ReactionRepository;
import fm.mixer.gateway.module.user.persistance.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ReactionService<ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> {

    private final ReactionRepository<ITEM, TABLE> repository;
    private final ReportService reportService;
    private final ResourceType resourceType;

    public List<UserReaction> react(final ReactionContainerEntity<ITEM, TABLE> item, UserReaction.TypeEnum reactionType) {
        checkReactionType(reactionType, false);

        final var user = getCurrentUser();

        if (UserReaction.TypeEnum.REPORT.equals(reactionType)) {
            reportService.report(user, item.getId(), resourceType);

            return ReactionMapper.toReactions(item.getReactions(), user);
        }

        // Find old record and change value flag. If there is no old record create new object with mapper and store it.
        final var reaction = repository.save(
            repository.findByUserAndItemAndType(user, item, ReactionMapper.toReactionType(reactionType)).map(reactionRecord -> {

                reactionRecord.setValue(ReactionMapper.toReactionValue(reactionType));

                return reactionRecord;
            }).orElse(ReactionMapper.toReactionEntity(repository.getEntityClass() , user, item, reactionType))
        );

        item.getReactions().remove(reaction);
        item.getReactions().add(reaction);

        return ReactionMapper.toReactions(item.getReactions(), user);
    }

    public List<UserReaction> removeReaction(final ReactionContainerEntity<ITEM, TABLE> item, UserReaction.TypeEnum reactionType) {
        checkReactionType(reactionType, true);

        final var user = getCurrentUser();
        final var reaction = repository.findByUserAndItemAndType(user, item, ReactionMapper.toReactionType(reactionType))
            .orElseThrow(ResourceNotFoundException::new);

        item.getReactions().remove(reaction);
        repository.delete(reaction);

        return ReactionMapper.toReactions(item.getReactions(), user);
    }

    private void checkReactionType(UserReaction.TypeEnum reaction, boolean remove) {
        // Default reaction is removed
        if (Objects.isNull(reaction)) {
            // Multiple reactions are possible - client must define which reaction to remove
            if (ReactionMapper.toAllowedUserReactions(resourceType).size() > 2) {
                throw new BadRequestException("ambiguous.reaction.call.error");
            }
            return;
        }

        // Every resource can be reported
        if (!remove && UserReaction.TypeEnum.REPORT.equals(reaction)) {
            return;
        }

        if (!ReactionMapper.toAllowedUserReactions(resourceType).contains(reaction)) {
            throw new BadRequestException("reaction.type.not.supported.error");
        }
    }

    private User getCurrentUser() {
        return UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);
    }
}
