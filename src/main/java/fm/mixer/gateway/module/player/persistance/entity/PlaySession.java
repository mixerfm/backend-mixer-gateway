package fm.mixer.gateway.module.player.persistance.entity;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixTrack;
import fm.mixer.gateway.module.user.persistance.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "play_session")
public class PlaySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mix_id", nullable = false)
    private Mix mix;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private MixTrack track;

    @Column(nullable = false)
    private String tracks;

    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private Boolean shuffle = false;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
