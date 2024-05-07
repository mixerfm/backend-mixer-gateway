package fm.mixer.gateway.api.user.service;

import fm.mixer.gateway.api.user.mapper.UserMapper;
import fm.mixer.gateway.api.v1.model.TestData;
import fm.mixer.gateway.client.TestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final TestApiClient testApiClient;

    public TestData getTestData() {
        return userMapper.mapToTestData(testApiClient.testGet().getBody());
    }
}
