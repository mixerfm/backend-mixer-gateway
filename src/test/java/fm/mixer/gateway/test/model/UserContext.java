package fm.mixer.gateway.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    @Builder.Default
    private String userId = "uid1";
    @Builder.Default
    private String email = "request@example.com";
    @Builder.Default
    private String device = "dev1";
    @Builder.Default
    private String country = "HR";
}
