package fm.mixer.gateway.module.mix.persistance.entity;

import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.react.persistance.entity.ReactionContainerEntity;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "mix_collection")
public class MixCollection implements ReactionContainerEntity<MixCollection, MixCollectionLike> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String name;

    private String description;

    private String avatar;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
    private Set<MixCollectionLike> reactions = new HashSet<>();

    @Column(nullable = false)
    private Integer numberOfReactions = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private VisibilityType visibility;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "mix_collection_tag_relation",
        joinColumns = {@JoinColumn(name = "collection_id")},
        inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<MixTag> tags;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_artist_mix_collection_relation",
        joinColumns = {@JoinColumn(name = "collection_id")},
        inverseJoinColumns = {@JoinColumn(name = "artist_id")}
    )
    @OrderColumn(name = "position")
    private List<UserArtist> artists;

    @PreUpdate
    @PrePersist
    private void updateNumberOfReactions() {
        numberOfReactions = reactions.size();
    }
}
