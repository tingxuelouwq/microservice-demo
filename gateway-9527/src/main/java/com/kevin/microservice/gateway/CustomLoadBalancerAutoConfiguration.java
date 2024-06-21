package com.kevin.microservice.gateway;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * kevin<br/>
 * 2024/6/19 10:15<br/>
 */
@LoadBalancerClients(defaultConfiguration = CustomLoadBalancerClientConfiguration.class)
public class CustomLoadBalancerAutoConfiguration {

}
