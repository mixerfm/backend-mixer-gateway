package fm.mixer.gateway.module.community.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.community.api.v1.CommentApiDelegate;
import fm.mixer.gateway.module.community.api.v1.model.Comment;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.api.v1.model.CreateOrEditComment;
import fm.mixer.gateway.module.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApiDelegate {

    private final CommunityService service;

    @Override
    public ResponseEntity<CommentList> getCommentList(String mixId, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getCommentList(mixId, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<Comment> createComment(String mixId, CreateOrEditComment createOrEditComment) {
        final var comment = service.createComment(mixId, createOrEditComment.getContent());

        return ResponseEntity.created(URI.create("/comments/" + comment.getIdentifier())).body(comment);
    }

    @Override
    public ResponseEntity<CommentList> getReplyList(String commentId, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getReplyList(commentId, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<Comment> createReply(String commentId, CreateOrEditComment createOrEditComment) {
        final var comment = service.createReply(commentId, createOrEditComment.getContent());

        return ResponseEntity.created(URI.create("/comments/" + comment.getIdentifier())).body(comment);
    }

    @Override
    public ResponseEntity<Comment> editComment(String commentId, CreateOrEditComment createOrEditComment) {
        return ResponseEntity.ok(service.editComment(commentId, createOrEditComment.getContent()));
    }

    @Override
    public ResponseEntity<Void> deleteComment(String commentId) {
        service.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserReaction>> react(String commentId, UserReaction userReaction) {
        return ResponseEntity
            .created(URI.create(String.format("/comments/%s/reactions", commentId)))
            .body(service.react(commentId, userReaction.getType()));
    }

    @Override
    public ResponseEntity<List<UserReaction>> removeReaction(String commentId) {
        return ResponseEntity.ok(service.removeReaction(commentId));
    }
}
