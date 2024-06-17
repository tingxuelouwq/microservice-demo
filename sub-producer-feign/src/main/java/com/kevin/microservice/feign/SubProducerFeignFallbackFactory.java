package com.kevin.microservice.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * kevin<br/>
 * 2021/12/22 16:27<br/>
 */
@Component
public class SubProducerFeignFallbackFactory implements FallbackFactory<SubProducerFeignService> {
    @Override
    public SubProducerFeignService create(Throwable cause) {
       return new SubProducerFeignService() {
           @Override
           public String subQuery(String name) {
               return "查询:" + name + "时降级";
           }
       };
    }
}
