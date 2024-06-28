package fm.mixer.gateway.module.user.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.module.user.api.v1.model.CreateUser;
import fm.mixer.gateway.module.user.api.v1.model.UpdateUser;
import fm.mixer.gateway.module.user.config.UserProfileColorConfig;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ValidateUserService {

    private final UserRepository repository;
    private final UserProfileColorConfig colorConfig;

    public void validateUpdateUserInput(final UpdateUser updateUser, final User user) {
        // Check if user is equal to currently active user
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();
        if (currentActiveUser.isEmpty() || !currentActiveUser.get().getId().equals(user.getId())) {
            throw new AccessForbiddenException();
        }

        // If user changed email, but that email is already in use
        if (!updateUser.getEmail().equals(user.getEmail()) && repository.existsByEmailAndActiveIsTrue(updateUser.getEmail())) {
            throw new BadRequestException("username.or.email.exists.error");
        }

        // If user changed username, but that username is already in use
        if (!updateUser.getUsername().equals(user.getIdentifier()) && repository.existsByIdentifierAndActiveIsTrue(updateUser.getUsername())) {
            throw new BadRequestException("username.or.email.exists.error");
        }

        validateDateOfBirth(updateUser.getDateOfBirth());
        validateProfileColor(updateUser.getProfileColor());
    }

    public void validateCreateUserInput(CreateUser createUser) {
        final var currentActiveUserEmail = UserPrincipalUtil.resolveUserEmailFromJwt();
        if (currentActiveUserEmail.isEmpty() || !currentActiveUserEmail.get().equals(createUser.getEmail())) {
            throw new AccessForbiddenException();
        }

        // Check if there is already user with this username or email
        if (repository.existsByEmailAndActiveIsTrueOrIdentifierAndActiveIsTrue(createUser.getEmail(), createUser.getUsername())) {
            throw new BadRequestException("username.or.email.exists.error");
        }

        validateDateOfBirth(createUser.getDateOfBirth());
        validateProfileColor(createUser.getProfileColor());
    }

    private void validateDateOfBirth(LocalDate dateOfBirth) {
        if (Objects.nonNull(dateOfBirth) && dateOfBirth.isAfter(LocalDate.now())) {
            throw new BadRequestException("dob.invalid.error");
        }
    }

    private void validateProfileColor(String profileColor) {
        if (Objects.nonNull(profileColor) && !colorConfig.getActive().contains(profileColor)) {
            throw new BadRequestException("profile.color.invalid.error");
        }
    }

    public void validateUserCanDelete(User user) {
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();
        if (currentActiveUser.isEmpty() || !currentActiveUser.get().getId().equals(user.getId())) {
            throw new AccessForbiddenException();
        }
    }
}
