package fm.mixer.gateway;

import fm.mixer.gateway.client.TestApiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {TestApiClient.class})
public class MixerGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MixerGatewayApplication.class, args);
    }
}
