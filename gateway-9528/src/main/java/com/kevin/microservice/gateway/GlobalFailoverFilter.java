package com.kevin.microservice.gateway;

import com.kevin.microservice.common.BizConstant;
import com.kevin.microservice.loadbalancer.FailoverRequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * kevin<br/>
 * 2024/6/17 15:32<br/>
 */
@Component
public class GlobalFailoverFilter implements GlobalFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String envHeaderValue = headers.getFirst(BizConstant.ENV_HEADER);
        logger.info("thread: {}, request-env: {}", Thread.currentThread().getName(), envHeaderValue);

        String envTag = FailoverRequestContextHolder.getEnvTag();
        if (StringUtils.hasLength(envTag)) {
            logger.info("自动故障转移, old-request-env: {}, new-request-env: {}", envHeaderValue, envTag);
            // 清除线程局部变量标记
            FailoverRequestContextHolder.clear();
            // 将部署环境标记放入请求头中
            ServerHttpRequest tokenRequest = exchange.getRequest().mutate()
                    .header(BizConstant.ENV_HEADER, envTag)
                    .build();
            ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
            return chain.filter(build);
        }

        return chain.filter(exchange);
    }
}
