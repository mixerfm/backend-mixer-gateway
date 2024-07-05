package fm.mixer.gateway.module.auth.api;

import fm.mixer.gateway.module.auth.api.v1.model.Device;
import fm.mixer.gateway.module.auth.api.v1.model.DeviceCommon;
import fm.mixer.gateway.module.auth.api.v1.model.DeviceList;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import fm.mixer.gateway.test.model.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String BASE_URL = "/devices";
    private static final String DEVICE_URL = BASE_URL + "/%s";

    @Test
    void shouldDoCrudOperationsOnDevice() throws Exception {
        // Empty GET - When
        final var getEmptyList = doGetRequest(BASE_URL);

        // Empty GET - Then
        assertThat(getEmptyList.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(getEmptyList, "get-device-list-empty.json", DeviceList.class);

        // Create - When
        final var createdList = doPostRequest(BASE_URL, "create-device.json");

        // Create - Then
        assertThat(createdList.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        final var deviceList = mapper.readValue(createdList.getContentAsString(), DeviceList.class);
        assertThat(deviceList.getDevices())
            .hasSize(1)
            .allSatisfy(device -> assertDevice(device, "Test", DeviceCommon.TypeEnum.ANDROID_TV));

        final var identifier = deviceList.getDevices().getFirst().getIdentifier();
        setUserContext(UserContext.builder().device(identifier).build());

        // Update - When
        final var updatedList = doPutRequest(String.format(DEVICE_URL, identifier), "update-device.json");

        // Update - Then
        assertThat(updatedList.getStatus()).isEqualTo(HttpStatus.OK.value());

        assertThat(mapper.readValue(updatedList.getContentAsString(), DeviceList.class).getDevices())
            .hasSize(1)
            .allSatisfy(device -> assertDevice(device, "Test2", DeviceCommon.TypeEnum.IPHONE));

        // Delete - When
        final var deletedList = doDeleteRequest(String.format(DEVICE_URL, identifier));

        // Delete - Then
        assertThat(deletedList.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(deletedList, "get-device-list-empty.json", DeviceList.class);
    }

    private void assertDevice(Device given, String name, DeviceCommon.TypeEnum type) {
        assertThat(given.getCurrent()).isTrue();
        assertThat(given.getName()).isEqualTo(name);
        assertThat(given.getType()).isEqualTo(type);
        assertThat(given.getIdentifier()).isNotNull();
    }
}