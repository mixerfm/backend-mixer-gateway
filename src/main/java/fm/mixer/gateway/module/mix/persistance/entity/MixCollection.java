package fm.mixer.gateway.module.mix.persistance.entity;

import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "mix_collection")
public class MixCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String name;

    private String description;

    private String avatar;

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

    @ManyToMany
    @JoinTable(
        name = "mix_collection_relation",
        joinColumns = {@JoinColumn(name = "collection_id")},
        inverseJoinColumns = {@JoinColumn(name = "mix_id")}
    )
    @OrderColumn(name = "position")
    private List<Mix> mixes;

    @ManyToMany
    @JoinTable(
        name = "mix_collection_tag_relation",
        joinColumns = {@JoinColumn(name = "collection_id")},
        inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<MixTag> tags;
}
