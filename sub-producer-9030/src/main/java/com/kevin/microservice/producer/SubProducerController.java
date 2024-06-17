package com.kevin.microservice.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * kevin<br/>
 * 2021/12/22 16:19<br/>
 */
@RestController
public class SubProducerController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/sub-query")
    public String subQuery(@RequestParam(value = "name") String name) {
        String result = "sub-query:" + name + ", serverPort=" + serverPort;
        System.out.println(result);
        return result;
    }

    @GetMapping("/**")
    public String defaultPath(HttpServletRequest request){
        return "hello world, uri:" + request.getRequestURI();
    }
}
