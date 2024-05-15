package fm.mixer.gateway.error.stub;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidArgumentsTestStub {

    public static final String VALIDATION_ERROR_MESSAGE = "Test integer must be positive number";

    @Positive(message = VALIDATION_ERROR_MESSAGE)
    public Integer testPath;
}
