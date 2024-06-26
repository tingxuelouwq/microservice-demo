package com.kevin.microservice.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * kevin<br/>
 * 2021/12/22 16:27<br/>
 */
@Component
public class ProducerFeignFallbackFactory implements FallbackFactory<ProducerFeignService> {
    @Override
    public ProducerFeignService create(Throwable cause) {
       return new ProducerFeignService() {
           @Override
           public String query(String name) {
               return "查询:" + name + "时降级";
           }

           @Override
           public String subQuery(String name) {
               return "二级查询:" + name + "时降级";
           }
       };
    }
}
