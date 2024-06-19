package com.kevin.microservice.loadbalancer;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * kevin<br/>
 * 2024/6/19 10:15<br/>
 */
@LoadBalancerClients(defaultConfiguration = GrayLoadBalancerClientConfiguration.class)
//@LoadBalancerClient(value = "microservice-consumer", configuration = GrayLoadBalancerClientConfiguration.class)
public class GrayLoadBalancerAutoConfiguration {
}
