package com.kevin.microservice.gateway;

import com.kevin.microservice.common.BizConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
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
        return chain.filter(exchange);
    }
}
