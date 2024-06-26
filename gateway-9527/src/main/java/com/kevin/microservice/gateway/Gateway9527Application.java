package com.kevin.microservice.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.kevin.microservice")
@EnableDiscoveryClient
//@LoadBalancerClients(defaultConfiguration = FailoverLoadBalancerClientConfiguration.class)
public class Gateway9527Application {

    public static void main(String[] args) {
        SpringApplication.run(Gateway9527Application.class, args);
    }

}
