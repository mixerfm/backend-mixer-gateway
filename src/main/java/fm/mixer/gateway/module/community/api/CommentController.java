package fm.mixer.gateway.module.community.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.module.community.api.v1.CommentApiDelegate;
import fm.mixer.gateway.module.community.api.v1.model.Comment;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.api.v1.model.CreateOrEditComment;
import fm.mixer.gateway.module.community.api.v1.model.UserReaction;
import fm.mixer.gateway.module.community.service.CommunityService;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.service.ReportService;
import fm.mixer.gateway.validation.annotation.OpenApiValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApiDelegate {

    private final CommunityService service;
    private final ReportService reportService;

    @Override
    @OpenApiValidation
    public ResponseEntity<CommentList> getCommentList(String mixId, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getCommentList(mixId, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Comment> createComment(String mixId, CreateOrEditComment createOrEditComment) {
        final var comment = service.createComment(mixId, createOrEditComment.getContent());

        return ResponseEntity.created(URI.create("/comments/" + comment.getIdentifier())).body(comment);
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<CommentList> getReplyList(String commentId, List<String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getReplyList(commentId, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Comment> createReply(String commentId, CreateOrEditComment createOrEditComment) {
        final var comment = service.createReply(commentId, createOrEditComment.getContent());

        return ResponseEntity.created(URI.create("/comments/" + comment.getIdentifier())).body(comment);
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Comment> editComment(String commentId, CreateOrEditComment createOrEditComment) {
        return ResponseEntity.ok(service.editComment(commentId, createOrEditComment.getContent()));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> deleteComment(String commentId) {
        service.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> react(String commentId, UserReaction userReaction) {
        if (UserReaction.TypeEnum.REPORT.equals(userReaction.getType())) {
            reportService.report(commentId, ResourceType.COMMENT);
        }
        else {
            checkReactionType(userReaction.getType());
            service.react(commentId, UserReaction.TypeEnum.LIKE.equals(userReaction.getType()));
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> removeReaction(String commentId) {
        service.removeReaction(commentId);

        return ResponseEntity.noContent().build();
    }

    private void checkReactionType(UserReaction.TypeEnum type) {
        if (!List.of(UserReaction.TypeEnum.LIKE, UserReaction.TypeEnum.DISLIKE).contains(type)) {
            throw new BadRequestException("reaction.type.not.supported.error");
        }
    }
}
