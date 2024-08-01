package fm.mixer.gateway.validation.persistance.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class DisallowedWordRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
    public boolean findInContent(String content) {
        final var query = entityManager.createNativeQuery("select exists (select 1 from disallowed_word dw where position(dw.word in lower(:content)) > 0)");

        query.setParameter("content", content);

        return (boolean) query.getSingleResult();
    }
}
