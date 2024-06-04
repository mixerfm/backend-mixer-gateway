package fm.mixer.gateway.module.play.persistance.entity;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixTrack;
import fm.mixer.gateway.module.user.persistance.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "play_session")
public class PlaySession {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "mix_id", nullable = false)
    private Mix mix;

    @OneToOne
    @JoinColumn(name = "track_id", nullable = false)
    private MixTrack track;

    @Column(nullable = false)
    private Duration duration;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
