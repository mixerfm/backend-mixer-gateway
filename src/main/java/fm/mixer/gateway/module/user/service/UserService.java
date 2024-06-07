package fm.mixer.gateway.module.user.service;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.user.api.v1.model.AvatarContent;
import fm.mixer.gateway.module.user.api.v1.model.CreateUser;
import fm.mixer.gateway.module.user.api.v1.model.GetUser;
import fm.mixer.gateway.module.user.api.v1.model.UpdateUser;
import fm.mixer.gateway.module.user.mapper.UserMapper;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.repository.UserFollowerRepository;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import fm.mixer.gateway.util.RandomIdentifierUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;
    private final ValidateUserService validation;
    private final UserRepository repository;
    private final UserFollowerRepository followerRepository;

    public GetUser getUser(String username) {
        final var user = repository.findByActiveIsTrueAndIdentifier(username).orElseThrow(ResourceNotFoundException::new);

        return mapper.toGetUser(user, resolveRelation(user));
    }

    /**
     * Will resolve relation between current active user and requested user.
     */
    private GetUser.RelationEnum resolveRelation(User user) {
        final var activeUser = UserPrincipalUtil.getCurrentActiveUser();

        if (activeUser.isEmpty()) {
            return null;
        }
        if (activeUser.get().getId().equals(user.getId())) {
            return GetUser.RelationEnum.SELF;
        }
        if (followerRepository.findByUser(activeUser.get()).stream().anyMatch(follows -> follows.getFollowsUser().getId().equals(user.getId()))) {
            return GetUser.RelationEnum.FOLLOWING;
        }

        return null;
    }

    public GetUser createUser(CreateUser createUserRequest) {
        // If username was not supplied, randomly generate it
        var username = createUserRequest.getUsername();
        if (Objects.isNull(username)) {
            username = RandomIdentifierUtil.randomIdentifier();
        }

        validation.validateCreateUserInput(createUserRequest.getEmail(), username, createUserRequest.getDateOfBirth());

        // Convert it to entity, save and return to the client
        final var user = mapper.toUserCreate(createUserRequest, uploadAvatar(createUserRequest.getAvatar()).orElse(null), username);
        repository.save(user);

        return mapper.toGetUser(user, GetUser.RelationEnum.SELF);
    }

    public GetUser updateUser(String username, UpdateUser updateUserRequest) {
        var user = repository.findByActiveIsTrueAndIdentifier(username).orElseThrow(ResourceNotFoundException::new);

        validation.validateUpdateUserInput(updateUserRequest.getEmail(), updateUserRequest.getUsername(), updateUserRequest.getDateOfBirth(), user);

        // Update avatar if user uploaded new one. The old one is deleted from CDN
        final var oldAvatar = user.getAvatar();
        var avatar = uploadAvatar(updateUserRequest.getAvatar())
            .map(newAvatar -> {
                deleteAvatar(oldAvatar);
                return newAvatar;
            })
            .orElse(user.getAvatar());

        // Update fields and save
        mapper.toUserUpdate(user, updateUserRequest, avatar);
        repository.save(user);

        return mapper.toGetUser(user, GetUser.RelationEnum.SELF);
    }

    @Transactional
    public void deleteUser(String username) {
        final var user = repository.findByActiveIsTrueAndIdentifier(username).orElseThrow(ResourceNotFoundException::new);

        validation.validateUserCanDelete(user);

        followerRepository.deleteUser(user);

        Optional.ofNullable(user.getSocialNetworks()).ifPresent(Set::clear);
        user.setAddress(null);
        user.setActive(false);
        repository.save(user);
    }

    private Optional<String> uploadAvatar(AvatarContent avatarContent) {
        if (Objects.isNull(avatarContent) || Objects.isNull(avatarContent.getBytes())) {
            return Optional.empty();
        }

        // TODO implement upload
        return Optional.of("");
    }

    private void deleteAvatar(String path) {
        // TODO implement
    }
}
