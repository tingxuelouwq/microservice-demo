package com.kevin.microservice.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.kevin.microservice")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.kevin.microservice"})
public class Producer9022Application {

    public static void main(String[] args) {
        SpringApplication.run(Producer9022Application.class, args);
    }

}
