package fm.mixer.gateway.test.container;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class ContainerTestBase {

    @Container
    @SuppressWarnings("unused")
    static final DatabaseTestContainer databaseContainer = DatabaseTestContainer.getInstance();
}
