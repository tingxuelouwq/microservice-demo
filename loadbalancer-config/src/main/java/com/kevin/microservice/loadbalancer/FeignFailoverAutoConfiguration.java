package com.kevin.microservice.loadbalancer;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * kevin<br/>
 * 2024/6/19 15:08<br/>
 */
@Configuration(proxyBeanMethods = false)
public class FeignFailoverAutoConfiguration {

    @Bean
    @ConditionalOnClass(RequestInterceptor.class)
    public FeignFailovernterceptor feignFailovernterceptor() {
        return new FeignFailovernterceptor();
    }
}
