package fm.mixer.gateway.module.app.mapper;

import fm.mixer.gateway.module.app.api.v1.model.ClientType;
import fm.mixer.gateway.module.app.api.v1.model.FeatureType;
import fm.mixer.gateway.module.app.api.v1.model.ServerApiType;
import fm.mixer.gateway.module.app.config.VersionsConfig;
import fm.mixer.gateway.test.UnitTest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ApplicationMapperUnitTest {

    private final ApplicationMapper mapper = Mappers.getMapper(ApplicationMapper.class);

    @Test
    void shouldMapToVersionList() {
        // Given
        final var apiType = ServerApiType.APPLICATION_API;
        final var clientType = ClientType.ANDROID_CLIENT;

        final var apis = Map.of(apiType, Instancio.create(VersionsConfig.VersionConfig.class));
        final var clients = Map.of(clientType, Instancio.create(VersionsConfig.VersionConfig.class));

        final var config = new VersionsConfig();
        config.setClients(clients);
        config.setApis(apis);

        // When
        final var result = mapper.toVersionList(config);

        // Then
        assertThat(result.getApis()).hasSize(1).allSatisfy(api -> {
            assertThat(api.getType()).isEqualTo(apiType);
            assertThat(api.getCurrentVersion()).isEqualTo(apis.get(apiType).getCurrentVersion());
            assertThat(api.getMinVersion()).isEqualTo(apis.get(apiType).getMinVersion());
        });
        assertThat(result.getClients()).hasSize(1).allSatisfy(client -> {
            assertThat(client.getType()).isEqualTo(clientType);
            assertThat(client.getCurrentVersion()).isEqualTo(clients.get(clientType).getCurrentVersion());
            assertThat(client.getMinVersion()).isEqualTo(clients.get(clientType).getMinVersion());
        });
    }

    @Test
    void shouldMapToFeatureList() {
        // Given
        final var features = Map.of(FeatureType.APPLICATION, true);

        // When
        final var result = mapper.toFeatureList(features);

        // Then
        assertThat(result.getFeatures()).hasSize(1).allSatisfy(feature -> {
            assertThat(feature.getAvailable()).isTrue();
            assertThat(feature.getType()).isEqualTo(FeatureType.APPLICATION);
        });
    }
}