package fm.mixer.gateway.module.community.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.community.api.v1.model.Comment;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.mapper.CommunityMapper;
import fm.mixer.gateway.module.community.persistance.entity.CommentEntity;
import fm.mixer.gateway.module.community.persistance.entity.CommentLike;
import fm.mixer.gateway.module.community.persistance.repository.CommentLikeRepository;
import fm.mixer.gateway.module.community.persistance.repository.CommentRepository;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.repository.MixRepository;
import fm.mixer.gateway.module.react.service.ReactionService;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.validation.model.SpamVariablePath;
import fm.mixer.gateway.validation.service.SpamDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityMapper mapper;
    private final MixRepository mixRepository;
    private final CommentRepository repository;
    private final CommentLikeRepository likeRepository;
    private final SpamDetectionService spamDetectionService;
    @Qualifier("commentReactionService")
    private final ReactionService<CommentEntity, CommentLike> reactionService;

    public CommentList getCommentList(String mixId, PaginationRequest paginationRequest) {
        final var commentList = repository.findAllByMixIdentifierAndParentCommentIsNull(mixId, paginationRequest.pageable());

        return mapper.toCommentList(commentList, paginationRequest);
    }

    @Transactional
    public Comment createComment(String mixId, String content) {
        spamDetectionService.checkContentForSpam(SpamVariablePath.COMMENT, content);

        final var mix = mixRepository.findByIdentifier(mixId).orElseThrow(ResourceNotFoundException::new);

        changeCommentCount(mix, 1);

        return mapper.toComment(repository.save(mapper.toCommentEntity(content, getCurrentUser(), mix)));
    }

    public CommentList getReplyList(String commentId, PaginationRequest paginationRequest) {
        final var replies = repository.findAllByParentCommentIdentifier(commentId, paginationRequest.pageable());

        return mapper.toCommentList(replies, paginationRequest);
    }

    @Transactional
    public Comment createReply(String commentId, String content) {
        spamDetectionService.checkContentForSpam(SpamVariablePath.COMMENT, content);

        final var comment = repository.findByIdentifierWithMix(commentId).orElseThrow(ResourceNotFoundException::new);

        final var reply = mapper.toCommentEntity(content, getCurrentUser(), comment.getMix());
        reply.setParentComment(comment);

        // This will also save comment
        changeReplyCount(comment, 1);
        changeCommentCount(comment.getMix(), 1);

        return mapper.toComment(repository.save(reply));
    }

    public Comment editComment(String commentId, String content) {
        spamDetectionService.checkContentForSpam(SpamVariablePath.COMMENT, content);

        final var comment = repository.findByIdentifier(commentId).orElseThrow(ResourceNotFoundException::new);

        if (!getCurrentUser().getId().equals(comment.getUser().getId())) {
            throw new AccessForbiddenException();
        }

        comment.setContent(content);

        return mapper.toComment(repository.save(comment));
    }

    @Transactional
    public void deleteComment(String commentId) {
        final var comment = repository.findByIdentifierWithMix(commentId).orElseThrow(ResourceNotFoundException::new);

        if (!getCurrentUser().getId().equals(comment.getUser().getId())) {
            throw new AccessForbiddenException();
        }

        changeCommentCount(comment.getMix(), -1);
        Optional.ofNullable(comment.getParentComment()).ifPresent(
            parentComment -> changeReplyCount(parentComment, -1)
        );

        // Delete all replies
        final var replies = repository.findAllByParentComment(comment);
        if (!replies.isEmpty()) {
            likeRepository.deleteAllByItemIn(replies);
            repository.deleteAll(replies);
            changeCommentCount(comment.getMix(), -replies.size());
        }

        likeRepository.deleteAllByItem(comment);
        repository.delete(comment);
    }

    public List<UserReaction> react(final String commentId, final UserReaction.TypeEnum reaction) {
        final var comment = repository.findByIdentifier(commentId).orElseThrow(ResourceNotFoundException::new);

        return reactionService.react(comment, reaction);
    }

    public List<UserReaction> removeReaction(final String commentId) {
        final var comment = repository.findByIdentifier(commentId).orElseThrow(ResourceNotFoundException::new);

        return reactionService.removeReaction(comment);
    }

    private void changeCommentCount(Mix mix, int byCount) {
        mix.setNumberOfComments(mix.getNumberOfComments() + byCount);

        mixRepository.save(mix);
    }

    private void changeReplyCount(CommentEntity comment, int byCount) {
        comment.setNumberOfReplies(comment.getNumberOfReplies() + byCount);

        repository.save(comment);
    }

    private User getCurrentUser() {
        return UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);
    }
}
