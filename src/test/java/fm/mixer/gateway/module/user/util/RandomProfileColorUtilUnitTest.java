package fm.mixer.gateway.module.user.util;

import fm.mixer.gateway.error.exception.InternalServerErrorException;
import fm.mixer.gateway.module.user.config.UserProfileColorConfig;
import fm.mixer.gateway.test.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@SuppressWarnings({"AccessStaticViaInstance", "InstantiationOfUtilityClass"})
class RandomProfileColorUtilUnitTest {

    @Test
    void shouldGenerateValidRandomProfileColor() {
        // Given
        final var colors = List.of("a", "b");
        final var config = mock(UserProfileColorConfig.class);

        when(config.getActive()).thenReturn(colors);

        // When
        final var result = new RandomProfileColorUtil(config).randomProfileColor();

        // Then
        assertThat(result).containsAnyOf("a", "b");
    }

    @Test
    void shouldGenerateValidRandomProfileColorWhenHaveOneColor() {
        // Given
        final var colors = List.of("test");
        final var config = mock(UserProfileColorConfig.class);

        when(config.getActive()).thenReturn(colors);

        // When
        final var result = new RandomProfileColorUtil(config).randomProfileColor();

        // Then
        assertThat(result).isEqualTo("test");
    }

    @Test
    void shouldThrowExceptionWhenGenerateRandomProfileColorWhenListIsEmpty() {
        // Given
        final List<String> colors = List.of();
        final var config = mock(UserProfileColorConfig.class);

        when(config.getActive()).thenReturn(colors);

        // When
        final var throwable = catchThrowable(() -> new RandomProfileColorUtil(config).randomProfileColor());

        // Then
        assertThat(throwable).isInstanceOf(InternalServerErrorException.class);
    }
}