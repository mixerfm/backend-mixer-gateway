package fm.mixer.gateway.module.mix.persistance.entity;

import fm.mixer.gateway.module.react.persistance.entity.ReactionEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mix_like")
public class MixLike extends ReactionEntity<Mix> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mix_id", nullable = false)
    private Mix item;
}
