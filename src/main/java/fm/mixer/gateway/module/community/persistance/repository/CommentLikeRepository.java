package fm.mixer.gateway.module.community.persistance.repository;

import fm.mixer.gateway.module.community.persistance.entity.Comment;
import fm.mixer.gateway.module.community.persistance.entity.CommentLike;
import fm.mixer.gateway.module.react.persistance.repository.ReactionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends ReactionRepository<Comment, CommentLike> {

    void deleteAllByItem(Comment comment);
}
