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
public class GlobalGrayFilter implements GlobalFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        logger.info("request-env: {}", headers.getFirst(BizConstant.ENV_HEADER));

        // 解析请求头，查看是否存在部署环境的请求头信息，如果存在则将其放置在ThreadLocal中
        if (headers.containsKey(BizConstant.ENV_HEADER)){
            String envValue = headers.getFirst(BizConstant.ENV_HEADER);
            if (StringUtils.hasLength(envValue)) {
                if (envValue.equals(BizConstant.ZS_ENV_VALUE)) {
                    // 设置总社部署环境标记
                    FailoverRequestContextHolder.setEnvTag(BizConstant.ZS_ENV_VALUE);
                } else if (envValue.equals(BizConstant.DB_ENV_VALUE)) {
                    // 设置东坝部署环境标记
                    FailoverRequestContextHolder.setEnvTag(BizConstant.DB_ENV_VALUE);
                }

                // 将部署环境标记放入请求头中
                ServerHttpRequest tokenRequest = exchange.getRequest().mutate()
                        .header(BizConstant.ENV_HEADER, FailoverRequestContextHolder.getEnvTag())
                        .build();
                ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
                return chain.filter(build);
            }
        }

        return chain.filter(exchange);
    }
}

