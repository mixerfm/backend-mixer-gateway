package fm.mixer.gateway.error.data;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

    @Builder.Default
    private final String errorId = UUID.randomUUID().toString();

    private final String status;
    private final String description;
}
