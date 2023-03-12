package com.kevin.microservice.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * kevin<br/>
 * 2021/12/22 16:19<br/>
 */
@RestController
public class ProducerController {

    @GetMapping("/query")
    public String query(@RequestParam(value = "name") String name) {
        System.out.println("query:" + name);
        return "query:" + name;
    }
}
