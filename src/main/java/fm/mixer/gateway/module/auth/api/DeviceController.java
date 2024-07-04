package fm.mixer.gateway.module.auth.api;

import fm.mixer.gateway.error.exception.InternalServerErrorException;
import fm.mixer.gateway.module.auth.api.v1.DeviceApiDelegate;
import fm.mixer.gateway.module.auth.api.v1.model.CreateOrUpdateDevice;
import fm.mixer.gateway.module.auth.api.v1.model.Device;
import fm.mixer.gateway.module.auth.api.v1.model.DeviceList;
import fm.mixer.gateway.module.auth.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class DeviceController implements DeviceApiDelegate {

    private final DeviceService service;

    @Override
    public ResponseEntity<DeviceList> getDeviceList() {
        return ResponseEntity.ok(service.getDeviceList());
    }

    @Override
    public ResponseEntity<DeviceList> createDevice(CreateOrUpdateDevice createDevice) {
        final var deviceList = service.createDevice(createDevice);
        final var identifier = deviceList.getDevices().stream()
            .filter(Device::getCurrent)
            .map(Device::getIdentifier)
            .findFirst().orElseThrow(() -> new InternalServerErrorException("Current device not set after creating"));

        return ResponseEntity.created(URI.create("/devices/" + identifier)).body(deviceList);
    }

    @Override
    public ResponseEntity<DeviceList> updateDevice(String deviceId, CreateOrUpdateDevice createOrUpdateDevice) {
        return ResponseEntity.ok(service.updateDevice(deviceId, createOrUpdateDevice));
    }

    @Override
    public ResponseEntity<DeviceList> deleteDevice(String deviceId) {
        return ResponseEntity.ok(service.deleteDevice(deviceId));
    }
}
