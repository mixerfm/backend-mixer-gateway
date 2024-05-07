package fm.mixer.gateway.api.user.controller;

import fm.mixer.gateway.api.user.service.UserService;
import fm.mixer.gateway.api.v1.TestApiDelegate;
import fm.mixer.gateway.api.v1.model.TestData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements TestApiDelegate {

    private final UserService userService;

    @Override
    public ResponseEntity<TestData> testGet() {
        return ResponseEntity.ok(userService.getTestData());
    }
}
