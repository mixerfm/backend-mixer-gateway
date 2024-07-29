package fm.mixer.gateway.module.user.mapper;

import fm.mixer.gateway.module.user.api.v1.model.SocialMedia;
import fm.mixer.gateway.module.user.api.v1.model.UpdateUser;
import fm.mixer.gateway.module.user.api.v1.model.UserCommon;
import fm.mixer.gateway.module.user.api.v1.model.UserRelation;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserSocialNetwork;
import fm.mixer.gateway.test.UnitTest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.util.SerializationUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@UnitTest
class UserMapperUnitTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapToGetUser() {
        // Given
        final var socialNetwork = Instancio.create(UserSocialNetwork.class);
        final var user = Instancio.of(User.class)
            .set(field(User::getSocialNetworks), List.of(socialNetwork))
            .create();

        // When
        final var result = mapper.toGetUser(user, UserRelation.FOLLOWING);

        // Then
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getActive()).isEqualTo(user.getActive());
        assertThat(result.getDisplayName()).isEqualTo(user.getName());
        assertThat(result.getAvatarUrl()).isEqualTo(user.getAvatar());
        assertThat(result.getBiography()).isEqualTo(user.getBiography());
        assertThat(result.getUsername()).isEqualTo(user.getIdentifier());
        assertThat(result.getRelation()).isEqualTo(UserRelation.FOLLOWING);
        assertThat(result.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        assertThat(result.getDateOfBirth()).isEqualTo(user.getDateOfBirth());
        assertThat(result.getProfileColor()).isEqualTo(user.getProfileColor());
        assertThat(result.getGender().name()).isEqualTo(user.getGender().name());
        assertThat(result.getNumberOfFollowers()).isEqualTo(user.getNumberOfFollowers());
        assertThat(result.getNumberOfFollowing()).isEqualTo(user.getNumberOfFollowing());
        assertThat(result.getType().name()).isEqualTo(user.getType().name() + "_USER");

        assertThat(result.getSocialMedia()).allSatisfy(media -> {
            assertThat(media.getUrl()).isEqualTo(socialNetwork.getUrl());
            assertThat(media.getType().name()).isEqualTo(socialNetwork.getType().name());
        });

        assertThat(result.getAddress().getCity()).isEqualTo(user.getAddress().getCity());
        assertThat(result.getAddress().getCountryCode()).isEqualTo(user.getAddress().getCountryCode());
        assertThat(result.getAddress().getLocation().getLatitude()).isEqualTo(user.getAddress().getLatitude());
        assertThat(result.getAddress().getLocation().getLongitude()).isEqualTo(user.getAddress().getLongitude());
    }

    @Test
    void shouldMapToUserCreateEntity() {
        // Given
        final var socialMedia = Instancio.create(SocialMedia.class);
        final var userCommon = Instancio.of(UserCommon.class)
            .set(field(UserCommon::getSocialMedia), List.of(socialMedia))
            .create();
        final var avatar = "avatar";
        final var identifier = "identifier";

        // When
        final var result = mapper.toUserCreateEntity(userCommon, avatar, identifier);

        // Then
        assertThat(result.getAvatar()).isEqualTo(avatar);
        assertThat(result.getIdentifier()).isEqualTo(identifier);

        assertThat(result.getEmail()).isEqualTo(userCommon.getEmail());
        assertThat(result.getName()).isEqualTo(userCommon.getDisplayName());
        assertThat(result.getBiography()).isEqualTo(userCommon.getBiography());
        assertThat(result.getDateOfBirth()).isEqualTo(userCommon.getDateOfBirth());
        assertThat(result.getPhoneNumber()).isEqualTo(userCommon.getPhoneNumber());
        assertThat(result.getProfileColor()).isEqualTo(userCommon.getProfileColor());
        assertThat(result.getGender().name()).isEqualTo(userCommon.getGender().name());

        assertThat(result.getActive()).isTrue();
        assertThat(result.getNumberOfFollowers()).isZero();
        assertThat(result.getNumberOfFollowing()).isZero();

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();

        assertThat(result.getSocialNetworks()).allSatisfy(network -> {
            assertThat(network.getUrl()).isEqualTo(socialMedia.getUrl());
            assertThat(network.getType().name()).isEqualTo(socialMedia.getType().name());
        });

        assertThat(result.getAddress().getCity()).isEqualTo(userCommon.getAddress().getCity());
        assertThat(result.getAddress().getCountryCode()).isEqualTo(userCommon.getAddress().getCountryCode());
        assertThat(result.getAddress().getLatitude()).isEqualTo(userCommon.getAddress().getLocation().getLatitude());
        assertThat(result.getAddress().getLongitude()).isEqualTo(userCommon.getAddress().getLocation().getLongitude());
    }

    @Test
    void shouldMapToUserUpdateEntity() {
        // Given
        final var user = Instancio.create(User.class);
        final var socialMedia = Instancio.create(SocialMedia.class);
        final var updateUser = Instancio.of(UpdateUser.class)
            .set(field(UpdateUser::getSocialMedia), List.of(socialMedia))
            .create();
        final var avatar = "avatar";

        final var userCopy = SerializationUtils.clone(user);

        // When
        mapper.toUserUpdateEntity(user, updateUser, avatar);

        // Then
        assertThat(user.getId()).isEqualTo(userCopy.getId());
        assertThat(user.getCreatedAt()).isEqualTo(userCopy.getCreatedAt());
        assertThat(user.getUpdatedAt()).isEqualTo(userCopy.getUpdatedAt());
        assertThat(user.getIdentifier()).isNotEqualTo(userCopy.getIdentifier());
        assertThat(user.getNumberOfFollowers()).isEqualTo(userCopy.getNumberOfFollowers());
        assertThat(user.getNumberOfFollowing()).isEqualTo(userCopy.getNumberOfFollowing());

        assertThat(user.getActive()).isTrue();
        assertThat(user.getAvatar()).isEqualTo(avatar);
        assertThat(user.getEmail()).isEqualTo(updateUser.getEmail());
        assertThat(user.getName()).isEqualTo(updateUser.getDisplayName());
        assertThat(user.getBiography()).isEqualTo(updateUser.getBiography());
        assertThat(user.getIdentifier()).isEqualTo(updateUser.getUsername());
        assertThat(user.getDateOfBirth()).isEqualTo(updateUser.getDateOfBirth());
        assertThat(user.getPhoneNumber()).isEqualTo(updateUser.getPhoneNumber());
        assertThat(user.getGender().name()).isEqualTo(updateUser.getGender().name());
        assertThat(user.getProfileColor()).isEqualTo(updateUser.getProfileColor());

        assertThat(user.getAddress().getCity()).isEqualTo(updateUser.getAddress().getCity());
        assertThat(user.getAddress().getCountryCode()).isEqualTo(updateUser.getAddress().getCountryCode());
        assertThat(user.getAddress().getLatitude()).isEqualTo(updateUser.getAddress().getLocation().getLatitude());
        assertThat(user.getAddress().getLongitude()).isEqualTo(updateUser.getAddress().getLocation().getLongitude());

        assertThat(user.getSocialNetworks()).allSatisfy(network -> {
            assertThat(network.getUrl()).isEqualTo(socialMedia.getUrl());
            assertThat(network.getType().name()).isEqualTo(socialMedia.getType().name());
        });
    }

    @Test
    void shouldMapToUserDeletedEntity() {
        // Given
        final var user = Instancio.create(User.class);
        final var profileColor = "black";
        final var deleted = "[deleted]";

        final var userCopy = SerializationUtils.clone(user);

        // When
        mapper.toUserDeletedEntity(user, profileColor);

        // Then
        assertThat(user.getId()).isEqualTo(userCopy.getId());
        assertThat(user.getCreatedAt()).isEqualTo(userCopy.getCreatedAt());
        assertThat(user.getUpdatedAt()).isEqualTo(userCopy.getUpdatedAt());
        assertThat(user.getIdentifier()).isEqualTo(userCopy.getIdentifier());

        assertThat(user.getActive()).isFalse();
        assertThat(user.getName()).isEqualTo(deleted);
        assertThat(user.getEmail()).isEqualTo(deleted);
        assertThat(user.getNumberOfFollowers()).isZero();
        assertThat(user.getNumberOfFollowing()).isZero();
        assertThat(user.getProfileColor()).isEqualTo(profileColor);

        assertThat(user.getAvatar()).isNull();
        assertThat(user.getGender()).isNull();
        assertThat(user.getAddress()).isNull();
        assertThat(user.getBiography()).isNull();
        assertThat(user.getDateOfBirth()).isNull();
        assertThat(user.getPhoneNumber()).isNull();
        // Social networks are required to clear manually
        assertThat(user.getSocialNetworks()).isNotNull();
    }
}