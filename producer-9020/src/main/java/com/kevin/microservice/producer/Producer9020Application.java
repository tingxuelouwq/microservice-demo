package com.kevin.microservice.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.kevin.microservice")
@EnableDiscoveryClient
public class Producer9020Application {

    public static void main(String[] args) {
        SpringApplication.run(Producer9020Application.class, args);
    }

}
