package fm.mixer.gateway.module.auth.persistance.entity;

import fm.mixer.gateway.module.auth.persistance.entity.model.DeviceType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_device")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String token;

    @Column(nullable = false)
    private boolean registered;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType type;
}
