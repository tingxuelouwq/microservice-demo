package com.kevin.microservice.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.kevin.microservice")
@EnableDiscoveryClient
public class SubProducer9031Application {

    public static void main(String[] args) {
        SpringApplication.run(SubProducer9031Application.class, args);
    }

}
