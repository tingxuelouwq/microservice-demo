package com.kevin.microservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * kevin<br/>
 * 2021/12/22 16:13<br/>
 */
//@FeignClient(value = "MICROSERVICE-PRODUCER", fallbackFactory = FeignFallbackFactory.class)
//@FeignClient(value = "MICROSERVICE-PRODUCER")
@FeignClient(value = "microservice-producer")
public interface ProducerFeignService {

    @GetMapping("/query")
    String query(@RequestParam(value = "name") String name);
}
