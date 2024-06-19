package com.kevin.microservice.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.kevin.microservice")
@EnableDiscoveryClient
public class WebSocket9050Application {

    public static void main(String[] args) {
        SpringApplication.run(WebSocket9050Application.class, args);
    }

}
