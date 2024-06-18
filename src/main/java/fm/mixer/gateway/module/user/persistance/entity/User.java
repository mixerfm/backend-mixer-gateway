package fm.mixer.gateway.module.user.persistance.entity;

import fm.mixer.gateway.module.user.persistance.entity.model.UserGender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String biography;

    private LocalDate dateOfBirth;

    private String avatar;

    private UserGender gender;

    @Column(nullable = false)
    private Boolean active;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserLocation address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSocialNetwork> socialNetworks;

    @Formula("(select count(*) from user_follower uf1 where uf1.follows_user_id = id)")
    private Integer numberOfFollowers = 0;

    @Formula("(select count(*) from user_follower uf2 where uf2.user_id = id)")
    private Integer numberOfFollowing = 0;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
