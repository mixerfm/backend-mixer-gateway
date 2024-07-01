package fm.mixer.gateway.module.mix.persistance.entity;

import fm.mixer.gateway.module.mix.persistance.entity.model.MixCollectionRelationId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(MixCollectionRelationId.class)
@Table(name = "mix_collection_relation")
public class MixCollectionRelation {

    @Id
    @Column(name = "collection_id")
    private Long collectionId;

    @Id
    @Column(nullable = false)
    private Short position;

    @OneToOne
    @JoinColumn(name = "mix_id", nullable = false)
    private Mix mix;
}
