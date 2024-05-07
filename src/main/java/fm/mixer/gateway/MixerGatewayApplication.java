package fm.mixer.gateway;

import fm.mixer.gateway.client.TestApiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients(clients = {TestApiClient.class})
public class MixerGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MixerGatewayApplication.class, args);
    }
}
