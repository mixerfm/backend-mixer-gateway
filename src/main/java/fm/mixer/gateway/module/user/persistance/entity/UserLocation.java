package fm.mixer.gateway.module.user.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "user_location")
public class UserLocation {

    @Id
    @Column(name = "user_id")
    private Long id;

    @MapsId
    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    private String countryCode;

    private String city;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
