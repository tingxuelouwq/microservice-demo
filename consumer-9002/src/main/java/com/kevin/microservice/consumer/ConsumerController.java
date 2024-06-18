package com.kevin.microservice.consumer;

import com.kevin.microservice.feign.ProducerFeignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * kevin<br/>
 * 2021/12/22 16:15<br/>
 */
@RestController
public class ConsumerController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private ProducerFeignService producerFeignService;

    @GetMapping("/simple-query")
    public String simpleQuery(@RequestParam(value = "name") String name, HttpServletRequest request) {
        String info = "appName: " + appName + ", serverPort: " + serverPort + ", request-gray: " + request.getHeader("gray-tag");
        logger.info(info);
        return info;
    }

    @GetMapping("/query")
    public String query(@RequestParam(value = "name") String name, HttpServletRequest request) {
        logger.info("appName: {}, serverPort: {}, request-gray: {}", appName, serverPort, request.getHeader("gray-tag"));
        return producerFeignService.query(name);
    }

    @GetMapping("/test")
    public void hystrixText() throws InterruptedException {
        for (int i = 0; i < 135; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(producerFeignService.query(Thread.currentThread().getName()));
                }
            }).start();
        }
        Thread.currentThread().join();
    }
}