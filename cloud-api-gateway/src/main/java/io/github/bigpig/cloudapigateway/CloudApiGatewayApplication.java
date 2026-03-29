package io.github.bigpig.cloudapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@EnableWebFluxSecurity
@SpringBootApplication
public class CloudApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudApiGatewayApplication.class, args);
    }

}
