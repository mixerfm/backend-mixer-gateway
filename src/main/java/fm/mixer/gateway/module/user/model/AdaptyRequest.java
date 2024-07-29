package fm.mixer.gateway.module.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdaptyRequest {

    @JsonProperty("adapty_check")
    private String identifier;

    @JsonProperty("customer_user_id")
    private String customerId;

    @JsonProperty("event_type")
    private AdaptyEvent event;
}
