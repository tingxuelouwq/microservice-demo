package com.kevin.microservice.consumer;

import com.kevin.microservice.feign.ProducerFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * kevin<br/>
 * 2021/12/22 16:15<br/>
 */
@RestController
public class ConsumerController {

    @Autowired
    private ProducerFeignService producerFeignService;

    @GetMapping("/query")
    public String query(@RequestParam(value = "name") String name) {
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
