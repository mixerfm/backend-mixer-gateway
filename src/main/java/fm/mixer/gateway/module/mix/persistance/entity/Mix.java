package fm.mixer.gateway.module.mix.persistance.entity;

import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "mix")
public class Mix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String name;

    private String description;

    private String avatar;

    private Integer playCount;

    @OneToMany(mappedBy = "mix")
    private Set<MixLike> likes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private VisibilityType visibility;

    @Column(nullable = false)
    private boolean nsfw;

    @ManyToMany
    @JoinTable(
        name = "mix_track_relation",
        joinColumns = {@JoinColumn(name = "mix_id")},
        inverseJoinColumns = {@JoinColumn(name = "track_id")}
    )
    @OrderColumn(name = "position")
    private List<MixTrack> tracks;

    @ManyToMany
    @JoinTable(
        name = "mix_tag_relation",
        joinColumns = {@JoinColumn(name = "mix_id")},
        inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    @OrderColumn(name = "position")
    private Set<MixTag> tags;

    @ManyToMany
    @JoinTable(
        name = "user_artist_mix_relation",
        joinColumns = {@JoinColumn(name = "mix_id")},
        inverseJoinColumns = {@JoinColumn(name = "artist_id")}
    )
    @OrderColumn(name = "position")
    private List<UserArtist> artists;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
