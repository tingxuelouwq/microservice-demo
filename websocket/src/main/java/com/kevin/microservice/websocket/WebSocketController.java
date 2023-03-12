package com.kevin.microservice.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * kevin<br/>
 * 2021/12/22 16:19<br/>
 */
@RestController
public class WebSocketController {

    @GetMapping("/query")
    public String query(@RequestParam(value = "name") String name) {
        String result = "query:" + name;
        System.out.println(result);
        return result;
    }
}
