package fm.mixer.gateway.test.container;

import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseTestContainer extends PostgreSQLContainer<DatabaseTestContainer> {

    private static volatile DatabaseTestContainer container;

    private DatabaseTestContainer() {
        super("postgres:alpine");
        withInitScript("data/init_data.sql");
    }

    public static DatabaseTestContainer getInstance() {
        if (container == null) {
            synchronized (DatabaseTestContainer.class) {
                if (container == null) {
                    container = new DatabaseTestContainer();
                }
            }
        }

        return container;
    }

    @Override
    public void start() {
        super.start();

        // Sets properties that are referenced in application.yml
        System.setProperty("SERVICE_DATABASE_URL", container.getJdbcUrl());
        System.setProperty("SERVICE_DATABASE_USERNAME", container.getUsername());
        System.setProperty("SERVICE_DATABASE_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        // Do nothing, let JVM handle stop
    }
}
