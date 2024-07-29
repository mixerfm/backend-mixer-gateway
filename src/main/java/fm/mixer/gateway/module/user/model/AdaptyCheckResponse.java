package fm.mixer.gateway.module.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdaptyCheckResponse {

    @JsonProperty("adapty_check_response")
    private String checkResponse;
}
