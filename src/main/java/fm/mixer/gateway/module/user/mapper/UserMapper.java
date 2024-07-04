package fm.mixer.gateway.module.user.mapper;

import fm.mixer.gateway.module.user.api.v1.model.Address;
import fm.mixer.gateway.module.user.api.v1.model.GetUser;
import fm.mixer.gateway.module.user.api.v1.model.SocialMediaType;
import fm.mixer.gateway.module.user.api.v1.model.UpdateUser;
import fm.mixer.gateway.module.user.api.v1.model.UserCommon;
import fm.mixer.gateway.module.user.api.v1.model.UserRelation;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserLocation;
import fm.mixer.gateway.module.user.persistance.entity.model.SocialNetworkType;
import fm.mixer.gateway.module.user.persistance.entity.model.UserGender;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Mapping(target = "displayName", source = "user.name")
    @Mapping(target = "socialMedia", source = "user.socialNetworks")
    @Mapping(target = "username", source = "user.identifier")
    @Mapping(target = "avatarUrl", source = "user.avatar")
    @Mapping(target = "relation", source = "relation")
    @Mapping(target = ".", source = "user")
    GetUser toGetUser(User user, UserRelation relation);

    @Mapping(target = "location", source = ".")
    Address toAddress(UserLocation address);

    // UserLocation is defined as mapping target so MapStruct can reuse entity reference
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    void toUserLocation(@MappingTarget UserLocation userLocation, Address address);

    @ValueMapping(target = "FACEBOOK", source = "FACEBOOK")
    @ValueMapping(target = "X", source = "X")
    @ValueMapping(target = "INSTAGRAM", source = "INSTAGRAM")
    @InheritInverseConfiguration
    SocialMediaType toSocialMedia(SocialNetworkType type);

    @ValueMapping(target = "MALE", source = "MALE")
    @ValueMapping(target = "FEMALE", source = "FEMALE")
    @ValueMapping(target = "OTHER", source = "OTHER")
    @InheritInverseConfiguration
    UserCommon.GenderEnum toGenderEnum(UserGender gender);

    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserLocation toUserLocation(Address address);

    @Mapping(target = "name", source = "userCommon.displayName")
    @Mapping(target = "socialNetworks", source = "userCommon.socialMedia")
    @Mapping(target = "numberOfFollowers", constant = "0")
    @Mapping(target = "numberOfFollowing", constant = "0")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = ".", source = "userCommon")
    @Mapping(target = "id", ignore = true)
    User toUserCreateEntity(UserCommon userCommon, String avatar, String identifier);

    // Social networks are required to clear manually
    @Mapping(target = "email", constant = "[deleted]")
    @Mapping(target = "name", constant = "[deleted]")
    @Mapping(target = "active", constant = "false")
    @Mapping(target = "profileColor", source = "profileColor")
    @Mapping(target = "phoneNumber", expression = "java(null)")
    @Mapping(target = "biography", expression = "java(null)")
    @Mapping(target = "dateOfBirth", expression = "java(null)")
    @Mapping(target = "avatar", expression = "java(null)")
    @Mapping(target = "gender", expression = "java(null)")
    @Mapping(target = "address", expression = "java(null)")
    @Mapping(target = "numberOfFollowers", constant = "0")
    @Mapping(target = "numberOfFollowing", constant = "0")
    void toUserDeletedEntity(@MappingTarget User user, String profileColor);

    @Mapping(target = "name", source = "updateUser.displayName")
    @Mapping(target = "socialNetworks", source = "updateUser.socialMedia")
    @Mapping(target = "identifier", source = "updateUser.username")
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = ".", source = "updateUser")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toUserUpdateEntity(@MappingTarget User user, UpdateUser updateUser, String avatar);

    @AfterMapping
    default void toUser(@MappingTarget User user) {
        Optional.ofNullable(user.getAddress()).ifPresent(userLocation -> userLocation.setUser(user));
        Optional.ofNullable(user.getSocialNetworks())
            .ifPresent(socialNetworks -> socialNetworks.forEach(socialNetwork -> socialNetwork.setUser(user)));
    }
}
