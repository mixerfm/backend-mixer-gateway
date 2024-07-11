package fm.mixer.gateway.module.user.mapper;

import fm.mixer.gateway.test.UnitTest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class UserNewsletterMapperUnitTest {

    private final UserNewsletterMapper mapper = Mappers.getMapper(UserNewsletterMapper.class);

    @Test
    void shouldMapToUserNewsletterEntity() {
        // Given
        final var email = "email@example.com";

        // When
        final var result = mapper.toUserNewsletterEntity(email);

        // Then
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getIdentifier()).isNotNull();
        assertThat(result.getId()).isNull();
    }
}