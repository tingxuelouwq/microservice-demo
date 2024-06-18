package com.kevin.microservice.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.kevin.microservice"})
@ComponentScan(basePackages = {"com.kevin.microservice"})
public class Consumer9002Application {

    public static void main(String[] args) {
        SpringApplication.run(Consumer9002Application.class, args);
    }

}
