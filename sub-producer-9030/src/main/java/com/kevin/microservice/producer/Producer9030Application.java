package com.kevin.microservice.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Producer9030Application {

    public static void main(String[] args) {
        SpringApplication.run(Producer9030Application.class, args);
    }

}
