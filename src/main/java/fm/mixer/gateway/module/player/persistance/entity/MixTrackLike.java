package fm.mixer.gateway.module.player.persistance.entity;

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
@Table(name = "mix_track_like")
public class MixTrackLike extends ReactionEntity<MixTrack> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mix_track_id", nullable = false)
    private MixTrack item;
}
