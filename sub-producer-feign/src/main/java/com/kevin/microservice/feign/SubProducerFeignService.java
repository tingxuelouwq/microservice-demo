package com.kevin.microservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * kevin<br/>
 * 2021/12/22 16:13<br/>
 */
@FeignClient(value = "microservice-sub-producer", fallbackFactory = SubProducerFeignFallbackFactory.class)
public interface SubProducerFeignService {

    @GetMapping("/sub-query")
    String subQuery(@RequestParam(value = "name") String name);
}
