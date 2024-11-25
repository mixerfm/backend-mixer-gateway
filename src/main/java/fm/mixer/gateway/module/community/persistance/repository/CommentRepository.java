package fm.mixer.gateway.module.community.persistance.repository;

import fm.mixer.gateway.module.community.persistance.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByIdentifier(String identifier);

    @Query("from CommentEntity c join fetch c.mix left join fetch c.parentComment where c.identifier = :identifier")
    Optional<CommentEntity> findByIdentifierWithMix(String identifier);

    Page<CommentEntity> findAllByMixIdentifierAndParentCommentIsNull(String mixId, Pageable pageable);

    Page<CommentEntity> findAllByParentCommentIdentifier(String parentCommentIdentifier, Pageable pageable);

    List<CommentEntity> findAllByParentComment(CommentEntity parent);
}
