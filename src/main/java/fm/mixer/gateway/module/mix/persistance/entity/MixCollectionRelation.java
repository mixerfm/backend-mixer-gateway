package fm.mixer.gateway.module.mix.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mix_collection_relation")
public class MixCollectionRelation {

    @Id
    @Column(name = "collection_id")
    private Long collectionId;

    @ManyToOne
    @JoinColumn(name = "mix_id", nullable = false)
    private Mix mix;

    @Column(nullable = false)
    private Short position;
}
