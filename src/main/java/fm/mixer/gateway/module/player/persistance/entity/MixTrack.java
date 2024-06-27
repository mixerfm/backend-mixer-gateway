package fm.mixer.gateway.module.player.persistance.entity;

import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "mix_track")
public class MixTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private String streamUrl;

    @Column(nullable = false)
    private Integer playCount;

    @OneToMany(mappedBy = "track", fetch = FetchType.EAGER)
    private Set<MixTrackLike> likes = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "artist_id")
    private UserArtist artist;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private MixAlbum album;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
