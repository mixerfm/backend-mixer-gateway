package fm.mixer.gateway.module.auth.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.client.holder.ClientContextHolder;
import fm.mixer.gateway.client.model.ClientContext;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.auth.api.v1.model.CreateOrUpdateDevice;
import fm.mixer.gateway.module.auth.api.v1.model.DeviceList;
import fm.mixer.gateway.module.auth.mapper.DeviceMapper;
import fm.mixer.gateway.module.auth.persistance.entity.UserDevice;
import fm.mixer.gateway.module.auth.persistance.repository.UserDeviceRepository;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.util.RandomIdentifierUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceMapper mapper;
    private final UserDeviceRepository repository;

    public DeviceList getDeviceList() {
        return getDeviceList(getCurrentUser());
    }

    public DeviceList createDevice(CreateOrUpdateDevice createDevice) {
        final var user = getCurrentUser();
        final var device = new UserDevice();

        mapper.toUserDevice(device, createDevice, user, RandomIdentifierUtil.randomIdentifier());
        repository.save(device);

        ClientContextHolder.set(new ClientContext(device.getIdentifier()));

        return getDeviceList(user);
    }

    public DeviceList updateDevice(String deviceId, CreateOrUpdateDevice updateDevice) {
        final var user = getCurrentUser();

        final var device = repository.findByIdentifierAndUser(deviceId, user)
            .orElseThrow(ResourceNotFoundException::new);

        mapper.toUserDevice(device, updateDevice, user, deviceId);
        repository.save(device);

        return getDeviceList(user);
    }

    public DeviceList deleteDevice(String deviceId) {
        final var user = getCurrentUser();

        final var device = repository.findByIdentifierAndUser(deviceId, user).orElseThrow(ResourceNotFoundException::new);

        repository.delete(device);

        return getDeviceList(user);
    }

    public void deleteAllDevices(User user) {
        repository.deleteAllByUser(user);
    }

    private DeviceList getDeviceList(User user) {
        return mapper.toDeviceList(repository.findAllByUser(user));
    }

    private User getCurrentUser() {
        return UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);
    }
}
