package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.module.user.api.v1.UserApiDelegate;
import fm.mixer.gateway.module.user.api.v1.model.CreateUser;
import fm.mixer.gateway.module.user.api.v1.model.GetUser;
import fm.mixer.gateway.module.user.api.v1.model.UpdateUser;
import fm.mixer.gateway.module.user.service.UserService;
import fm.mixer.gateway.validation.annotation.OpenApiValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApiDelegate {

    private final UserService service;

    @Override
    public ResponseEntity<GetUser> getCurrentActiveUser() {
        return ResponseEntity.ok(service.getCurrentUser());
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<GetUser> getUser(String username) {
        return ResponseEntity.ok(service.getUser(username));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<GetUser> createUser(CreateUser createUser) {
        final var user = service.createUser(createUser);

        return ResponseEntity.created(URI.create("/users/" + user.getUsername())).body(user);
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<GetUser> updateUser(String username, UpdateUser user) {
        return ResponseEntity.ok(service.updateUser(username, user));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> deleteUser(String username) {
        service.deleteUser(username);

        return ResponseEntity.noContent().build();
    }
}
