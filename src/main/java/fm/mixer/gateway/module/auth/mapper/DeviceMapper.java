package fm.mixer.gateway.module.auth.mapper;

import fm.mixer.gateway.client.holder.ClientContextHolder;
import fm.mixer.gateway.module.auth.api.v1.model.CreateOrUpdateDevice;
import fm.mixer.gateway.module.auth.api.v1.model.Device;
import fm.mixer.gateway.module.auth.api.v1.model.DeviceCommon;
import fm.mixer.gateway.module.auth.api.v1.model.DeviceList;
import fm.mixer.gateway.module.auth.persistance.entity.UserDevice;
import fm.mixer.gateway.module.auth.persistance.entity.model.DeviceType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

import java.util.List;

@Mapper
public interface DeviceMapper {

    @ValueMapping(target = "ANDROID_TV", source = "ANDROID_TV")
    @ValueMapping(target = "ANDROID_TABLET", source = "ANDROID_TABLET")
    @ValueMapping(target = "ANDROID_PHONE", source = "ANDROID_PHONE")
    @ValueMapping(target = "ANDROID_WATCH", source = "ANDROID_WATCH")
    @ValueMapping(target = "APPLE_WATCH", source = "APPLE_WATCH")
    @ValueMapping(target = "IPHONE", source = "IPHONE")
    @ValueMapping(target = "IPAD", source = "IPAD")
    @ValueMapping(target = "PLAY_STATION", source = "PLAY_STATION")
    @ValueMapping(target = "WEB", source = "WEB")
    @ValueMapping(target = "XBOX", source = "XBOX")
    @InheritInverseConfiguration
    DeviceCommon.TypeEnum toType(DeviceType deviceType);

    @Mapping(target = "current", source = ".", qualifiedByName = "toCurrentDevice")
    Device toDevice(UserDevice userDevice);

    @Named("toCurrentDevice")
    default Boolean toCurrentDevice(UserDevice userDevice) {
        return ClientContextHolder.get()
            .map(clientContext -> userDevice.getIdentifier().equals(clientContext.deviceId()))
            .orElse(false);
    }

    List<Device> toDeviceListList(List<UserDevice> userDevice);

    default DeviceList toDeviceList(List<UserDevice> devices) {
        return new DeviceList().devices(toDeviceListList(devices));
    }

    @Mapping(target = "name", source = "device.name")
    @Mapping(target = "token", source = "device.token")
    @Mapping(target = "type", source = "device.type")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "registered", constant = "false")
    @Mapping(target = "id", ignore = true)
    void toUserDevice(@MappingTarget UserDevice userDevice, CreateOrUpdateDevice device, User user, String identifier);
}
