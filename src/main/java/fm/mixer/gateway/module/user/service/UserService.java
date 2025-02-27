package fm.mixer.gateway.module.user.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.auth.service.DeviceService;
import fm.mixer.gateway.module.user.api.v1.model.AvatarContent;
import fm.mixer.gateway.module.user.api.v1.model.CreateUser;
import fm.mixer.gateway.module.user.api.v1.model.GetUser;
import fm.mixer.gateway.module.user.api.v1.model.UpdateUser;
import fm.mixer.gateway.module.user.api.v1.model.UserRelation;
import fm.mixer.gateway.module.user.config.UserProfileColorConfig;
import fm.mixer.gateway.module.user.mapper.UserMapper;
import fm.mixer.gateway.module.user.persistance.repository.UserFollowerRepository;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import fm.mixer.gateway.module.user.util.RandomProfileColorUtil;
import fm.mixer.gateway.module.user.util.UserRelationUtil;
import fm.mixer.gateway.util.RandomIdentifierUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;
    private final UserRepository repository;
    private final DeviceService deviceService;
    private final ValidateUserService validation;
    private final NewsletterService newsletterService;
    private final UserFollowerRepository followerRepository;
    private final UserProfileColorConfig profileColorConfig;

    @Transactional
    public GetUser getCurrentUser() {
        return mapper.toGetUser(UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new), UserRelation.SELF);
    }

    public GetUser getUser(String username) {
        final var user = repository.findActiveByIdentifier(username).orElseThrow(ResourceNotFoundException::new);

        return mapper.toGetUser(user, UserRelationUtil.resolveRelation(user));
    }

    public GetUser createUser(CreateUser createUserRequest) {
        // If username was not supplied, randomly generate it
        if (!StringUtils.hasText(createUserRequest.getUsername())) {
            createUserRequest.setUsername(RandomIdentifierUtil.randomIdentifier());
        }
        if (!StringUtils.hasText(createUserRequest.getProfileColor())) {
            createUserRequest.setProfileColor(RandomProfileColorUtil.randomProfileColor());
        }

        validation.validateCreateUserInput(createUserRequest);

        // Convert it to entity, save and return to the client
        final var user = mapper.toUserCreateEntity(
            createUserRequest,
            uploadAvatar(createUserRequest.getAvatar()).orElse(null),
            createUserRequest.getUsername()
        );
        repository.save(user);

        // Subscribe user to the newsletter - ignore if user already sing-up for the newsletter
        try {
            newsletterService.subscribe(user.getEmail());
        }
        catch (BadRequestException ignore) {
        }

        return mapper.toGetUser(user, UserRelation.SELF);
    }

    public GetUser updateUser(String username, UpdateUser updateUserRequest) {
        var user = repository.findActiveByIdentifier(username).orElseThrow(ResourceNotFoundException::new);

        validation.validateUpdateUserInput(updateUserRequest, user);

        // Update avatar if user uploaded new one. The old one is deleted from CDN
        final var oldAvatar = user.getAvatar();
        var avatar = uploadAvatar(updateUserRequest.getAvatar())
            .map(newAvatar -> {
                deleteAvatar(oldAvatar);
                return newAvatar;
            })
            .orElse(user.getAvatar());

        // Update fields and save
        mapper.toUserUpdateEntity(user, updateUserRequest, avatar);
        repository.save(user);

        return mapper.toGetUser(user, UserRelation.SELF);
    }

    @Transactional
    public void deleteUser(String username) {
        final var user = repository.findActiveByIdentifier(username).orElseThrow(ResourceNotFoundException::new);

        validation.validateUserCanDelete(user);

        followerRepository.deleteUser(user);
        deviceService.deleteAllDevices(user);

        deleteAvatar(user.getAvatar());
        Optional.ofNullable(user.getSocialNetworks()).ifPresent(List::clear);
        mapper.toUserDeletedEntity(user, profileColorConfig.getInactive());

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
