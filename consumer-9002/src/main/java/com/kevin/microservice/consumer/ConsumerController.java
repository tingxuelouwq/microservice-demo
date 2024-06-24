package com.kevin.microservice.consumer;

import com.kevin.microservice.BizConstant;
import com.kevin.microservice.feign.ProducerFeignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/simple-query")
    public String simpleQuery(@RequestParam(value = "name") String name, HttpServletRequest request) {
        String info = "appName: " + appName + ", serverPort: " + serverPort + ", request-env: " + request.getHeader(BizConstant.ENV_HEADER) + ", thread: " + Thread.currentThread().getName();
        logger.info(info);
        return info;
    }

    @GetMapping("/query")
    public String query(@RequestParam(value = "name") String name, HttpServletRequest request) {
        logger.info("appName: {}, serverPort: {}, request-env: {}", appName, serverPort, request.getHeader(BizConstant.ENV_HEADER) + ", thread: " + Thread.currentThread().getName());
        return producerFeignService.query(name);
    }

    @GetMapping("/sub-query")
    public String subQuery(@RequestParam(value = "name") String name, HttpServletRequest request) {
        logger.info("appName: {}, serverPort: {}, request-env: {}", appName, serverPort, request.getHeader(BizConstant.ENV_HEADER) + ", thread: " + Thread.currentThread().getName());
        return producerFeignService.subQuery(name);
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

    @GetMapping("/getAllServices")
    public List<String> getAllServices() {
        return discoveryClient.getServices();
    }

    @GetMapping("/getInstances")
    public List<ServiceInstance> getInstances(String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }

    @GetMapping("/getServiceUri")
    public String getServiceUri(String serviceId) {
        return loadBalancerClient.choose(serviceId).getUri().toString();
    }
}
