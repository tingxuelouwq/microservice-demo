package com.kevin.microservice.feign.loadbalancer;

import feign.Client;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.OnRetryNotEnabledCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * kevin<br/>
 * 2024/6/23 17:00<br/>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LoadBalancerProperties.class)
public class CustomFeignLoadBalancerConfiguration {

    @Bean
    @Conditional(OnRetryNotEnabledCondition.class)
    public Client feignClient(LoadBalancerClient loadBalancerClient, LoadBalancerProperties properties,
                              LoadBalancerClientFactory loadBalancerClientFactory) {
        return new CustomFeignBlockingLoadBalancerClient(new Client.Default(null, null), loadBalancerClient, properties,
                loadBalancerClientFactory);
    }
}
