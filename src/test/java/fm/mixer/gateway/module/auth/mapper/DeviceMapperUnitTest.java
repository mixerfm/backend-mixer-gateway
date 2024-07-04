package fm.mixer.gateway.module.auth.mapper;

import fm.mixer.gateway.client.holder.ClientContextHolder;
import fm.mixer.gateway.client.model.ClientContext;
import fm.mixer.gateway.module.auth.api.v1.model.CreateOrUpdateDevice;
import fm.mixer.gateway.module.auth.persistance.entity.UserDevice;
import fm.mixer.gateway.module.auth.persistance.entity.model.DeviceType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class DeviceMapperUnitTest {

    final DeviceMapper mapper = Mappers.getMapper(DeviceMapper.class);

    @ParameterizedTest
    @EnumSource(DeviceType.class)
    void shouldMapToType(DeviceType given) {
        // When
        final var result = mapper.toType(given);

        // Then
        assertThat(result.name()).isEqualTo(given.name());
    }

    @Test
    void shouldMapToDeviceList() {
        // Given
        final var device = Instancio.create(UserDevice.class);
        final var context = mock(ClientContext.class);
        when(context.deviceId()).thenReturn(device.getIdentifier());

        try (final var holder = mockStatic(ClientContextHolder.class)) {
            holder.when(ClientContextHolder::get).thenReturn(Optional.of(context));

            // When
            final var result = mapper.toDeviceList(List.of(device));

            // Then
            assertThat(result.getDevices()).hasSize(1)
                .allSatisfy(dev -> {
                    assertThat(dev.getCurrent()).isTrue();
                    assertThat(dev.getName()).isEqualTo(device.getName());
                    assertThat(dev.getIdentifier()).isEqualTo(device.getIdentifier());
                    assertThat(dev.getType().name()).isEqualTo(device.getType().name());
                });
        }
    }

    @Test
    void shouldMapToUserDevice() {
        // Given
        final var userDevice = new UserDevice();
        final var user = Instancio.create(User.class);
        final var device = Instancio.create(CreateOrUpdateDevice.class);

        // When
        mapper.toUserDevice(userDevice, device, user, "test");

        // Then
        assertThat(userDevice.getIdentifier()).isEqualTo("test");
        assertThat(userDevice.getUser()).isEqualTo(user);
        assertThat(userDevice.getName()).isEqualTo(device.getName());
        assertThat(userDevice.getToken()).isEqualTo(device.getToken());
        assertThat(userDevice.getType().name()).isEqualTo(device.getType().name());

        assertThat(userDevice.getId()).isNull();
    }
}