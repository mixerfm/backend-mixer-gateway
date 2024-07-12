package fm.mixer.gateway.module.community.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.community.api.v1.model.Comment;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.mapper.CommunityMapper;
import fm.mixer.gateway.module.community.persistance.repository.CommentLikeRepository;
import fm.mixer.gateway.module.community.persistance.repository.CommentRepository;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.repository.MixRepository;
import fm.mixer.gateway.module.user.persistance.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityMapper mapper;
    private final MixRepository mixRepository;
    private final CommentRepository repository;
    private final CommentLikeRepository likeRepository;

    public CommentList getCommentList(String mixId, PaginationRequest paginationRequest) {
        final var commentList = repository.findAllByMixIdentifierAndParentCommentIsNull(mixId, paginationRequest.pageable());

        return mapper.toCommentList(commentList, paginationRequest);
    }

    public Comment createComment(String mixId, String content) {
        final var mix = mixRepository.findByIdentifier(mixId).orElseThrow(ResourceNotFoundException::new);

        changeCommentCount(mix, 1);

        return mapper.toComment(repository.save(mapper.toCommentEntity(content, getCurrentUser(), mix)));
    }

    public CommentList getReplyList(String commentId, PaginationRequest paginationRequest) {
        final var replies = repository.findAllByParentCommentIdentifier(commentId, paginationRequest.pageable());

        return mapper.toCommentList(replies, paginationRequest);
    }

    public Comment createReply(String commentId, String content) {
        final var comment = repository.findByIdentifierWithMix(commentId).orElseThrow(ResourceNotFoundException::new);

        final var reply = mapper.toCommentEntity(content, getCurrentUser(), comment.getMix());
        reply.setParentComment(comment);

        // This will also save comment
        changeReplyCount(comment, 1);
        changeCommentCount(comment.getMix(), 1);

        return mapper.toComment(repository.save(reply));
    }

    public Comment editComment(String commentId, String content) {
        final var comment = repository.findByIdentifier(commentId).orElseThrow(ResourceNotFoundException::new);

        if (!getCurrentUser().getId().equals(comment.getUser().getId())) {
            throw new AccessForbiddenException();
        }

        comment.setContent(content);

        return mapper.toComment(repository.save(comment));
    }

    public void deleteComment(String commentId) {
        final var comment = repository.findByIdentifierWithMix(commentId).orElseThrow(ResourceNotFoundException::new);

        if (!getCurrentUser().getId().equals(comment.getUser().getId())) {
            throw new AccessForbiddenException();
        }

        changeCommentCount(comment.getMix(), -1);
        Optional.ofNullable(comment.getParentComment()).ifPresent(
            parentComment -> changeReplyCount(parentComment, -1)
        );

        likeRepository.deleteAllByComment(comment);
        repository.delete(comment);
    }

    public void react(final String commentId, final boolean like) {
        final var user = getCurrentUser();
        final var comment = repository.findByIdentifier(commentId).orElseThrow(ResourceNotFoundException::new);

        // Find old record and change "liked" flag. If there is no old record create new object with mapper and store it.
        comment.getLikes().add(likeRepository.save(
            likeRepository.findByUserAndComment(user, comment).map(likeComment -> {
                likeComment.setLiked(like);
                return likeComment;
            }).orElse(mapper.toCommentLikeEntity(user, comment, like))
        ));
    }

    public void removeReaction(String commentId) {
        final var user = getCurrentUser();
        final var comment = repository.findByIdentifier(commentId).orElseThrow(ResourceNotFoundException::new);
        final var reaction = likeRepository.findByUserAndComment(user, comment).orElseThrow(ResourceNotFoundException::new);

        comment.getLikes().remove(reaction);
        likeRepository.delete(reaction);
    }

    private void changeCommentCount(Mix mix, int byCount) {
        mix.setNumberOfComments(mix.getNumberOfComments() + byCount);

        mixRepository.save(mix);
    }

    private void changeReplyCount(fm.mixer.gateway.module.community.persistance.entity.Comment comment, int byCount) {
        comment.setNumberOfReplies(comment.getNumberOfReplies() + byCount);

        repository.save(comment);
    }

    private User getCurrentUser() {
        return UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);
    }
}
