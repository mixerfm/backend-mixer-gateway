package fm.mixer.gateway.module.community.persistance.repository;

import fm.mixer.gateway.module.community.persistance.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdentifier(String identifier);

    Page<Comment> findAllByMixIdentifierAndParentCommentIsNull(String mixId, Pageable pageable);

    Page<Comment> findAllByParentCommentIdentifier(String parentCommentIdentifier, Pageable pageable);
}
