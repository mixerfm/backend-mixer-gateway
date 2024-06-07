package fm.mixer.gateway.test;

import fm.mixer.gateway.test.container.ContainerTestBase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@IntegrationTest
public abstract class BaseIntegrationTest extends ContainerTestBase {
}
