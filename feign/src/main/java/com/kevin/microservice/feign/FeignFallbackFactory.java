package com.kevin.microservice.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * kevin<br/>
 * 2021/12/22 16:27<br/>
 */
@Component
public class FeignFallbackFactory implements FallbackFactory<FeignService> {
    @Override
    public FeignService create(Throwable cause) {
       return new FeignService() {
           @Override
           public String query(String name) {
               return "查询:" + name + "时降级";
           }
       };
    }
}
