package fm.mixer.gateway.module.user.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.BadRequestException;
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

    public void validateUpdateUserInput(String newEmail, String newIdentifier, LocalDate newDateOfBirth, User user) {
        // Check if user is equal to currently active user
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();
        if (currentActiveUser.isEmpty() || !currentActiveUser.get().getId().equals(user.getId())) {
            throw new AccessForbiddenException();
        }

        // If user changed email, but that email is already in use
        if (!newEmail.equals(user.getEmail()) && repository.existsByEmailAndActiveIsTrue(newEmail)) {
            throw new BadRequestException("username.or.email.exists.error");
        }

        // If user changed username, but that username is already in use
        if (!newIdentifier.equals(user.getIdentifier()) && repository.existsByIdentifierAndActiveIsTrue(newIdentifier)) {
            throw new BadRequestException("username.or.email.exists.error");
        }

        if (Objects.nonNull(newDateOfBirth) && newDateOfBirth.isAfter(LocalDate.now())) {
            throw new BadRequestException("dob.invalid.error");
        }
    }

    public void validateCreateUserInput(String email, String identifier, LocalDate dateOfBirth) {
        final var currentActiveUserEmail = UserPrincipalUtil.resolveUserEmailFromJwt();
        if (currentActiveUserEmail.isEmpty() || !currentActiveUserEmail.get().equals(email)) {
            throw new AccessForbiddenException();
        }

        // Check if there is already user with this username or email
        if (repository.existsByEmailAndActiveIsTrueOrIdentifierAndActiveIsTrue(email, identifier)) {
            throw new BadRequestException("username.or.email.exists.error");
        }

        if (Objects.nonNull(dateOfBirth) && dateOfBirth.isAfter(LocalDate.now())) {
            throw new BadRequestException("dob.invalid.error");
        }
    }

    public void validateUserCanDelete(User user) {
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();
        if (currentActiveUser.isEmpty() || !currentActiveUser.get().getId().equals(user.getId())) {
            throw new AccessForbiddenException();
        }
    }
}
