package fm.mixer.gateway.module.play.persistance.entity;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.user.persistance.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "play_session_history")
public class PlaySessionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "mix_id", nullable = false)
    private Mix mix;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
