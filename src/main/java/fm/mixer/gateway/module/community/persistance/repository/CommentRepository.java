package fm.mixer.gateway.module.community.persistance.repository;

import fm.mixer.gateway.module.community.persistance.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdentifier(String identifier);

    @Query("from Comment c join fetch c.mix left join fetch c.parentComment where c.identifier = :identifier")
    Optional<Comment> findByIdentifierWithMix(String identifier);

    Page<Comment> findAllByMixIdentifierAndParentCommentIsNull(String mixId, Pageable pageable);

    Page<Comment> findAllByParentCommentIdentifier(String parentCommentIdentifier, Pageable pageable);

    List<Comment> findAllByParentComment(Comment parent);
}
