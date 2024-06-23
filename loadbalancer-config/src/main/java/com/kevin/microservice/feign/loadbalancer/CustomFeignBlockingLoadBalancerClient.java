package com.kevin.microservice.feign.loadbalancer;

import com.kevin.microservice.BizConstant;
import com.kevin.microservice.RequestContextHolder;
import feign.Client;
import feign.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * kevin<br/>
 * 2024/6/23 16:55<br/>
 */
public class CustomFeignBlockingLoadBalancerClient extends FeignBlockingLoadBalancerClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CustomFeignBlockingLoadBalancerClient(Client delegate, LoadBalancerClient loadBalancerClient,
                                                 LoadBalancerProperties properties,
                                                 LoadBalancerClientFactory loadBalancerClientFactory) {
        super(delegate, loadBalancerClient, properties, loadBalancerClientFactory);
    }

    @Override
    protected Request buildRequest(Request request, String reconstructedUrl) {
        return Request.create(request.httpMethod(), reconstructedUrl, enhanceRequestHeader(request.headers()), request.body(),
                request.charset(), request.requestTemplate());
    }

    private Map<String, Collection<String>> enhanceRequestHeader(Map<String, Collection<String>> headers) {
        String envTag = RequestContextHolder.getEnvTag();
        if (StringUtils.hasLength(envTag)) {
            // 清除线程局部变量标记
            RequestContextHolder.clear();
            // 将部署环境标记放入请求头中
            String envHeadValue = "";
            // Map是一个Collections$UnmodifiableMap，value是一个
            Map<String, Collection<String>> newHeaders = new HashMap<>();
            for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                Collection<String> value = entry.getValue();
                if (!key.equals(BizConstant.ENV_HEADER)) {
                    newHeaders.put(key, value);
                } else {
                    envHeadValue = ((List<String>) value).get(0);
                    newHeaders.put(key, Collections.unmodifiableList(List.of(envTag)));
                }
            }

            logger.info("自动故障转移, old-request-env: {}, new-request-env: {}", envHeadValue, envTag);

            return Collections.unmodifiableMap(newHeaders);
        }
        return headers;
    }
}
