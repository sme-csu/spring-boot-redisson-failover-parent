package com.microsoft.aa.haproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class HealthCheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthCheckApplication.class, args);
    }

}
