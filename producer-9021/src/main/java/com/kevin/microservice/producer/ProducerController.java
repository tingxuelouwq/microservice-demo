package com.kevin.microservice.producer;

import com.kevin.microservice.feign.SubProducerFeignService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProducerController {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private SubProducerFeignService subProducerFeignService;

    @GetMapping("/query")
    public String query(@RequestParam(value = "name") String name) {
        String result = "query:" + name + ", serverPort=" + serverPort;
        System.out.println(result);
        return result;
    }

    @GetMapping("/**")
    public String defaultPath(HttpServletRequest request){
        return "hello world, uri:" + request.getRequestURI();
    }

    @GetMapping("/sub-query")
    public String subQuery(@RequestParam(value = "name") String name) {
        String result = subProducerFeignService.subQuery(name);
        System.out.println(result);
        return result;
    }
}
