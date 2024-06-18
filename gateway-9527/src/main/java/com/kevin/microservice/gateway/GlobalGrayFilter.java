package com.kevin.microservice.gateway;

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
        logger.info("gateway-gray: {}", headers.getFirst("gray"));
//        if (headers.containsKey("gray")) {
//            String gray = headers.getFirst("gray");
//            if ("true".equals(gray)) {
//                ServerHttpRequest newRequest = request.mutate().header("gray", "true").build();
//                exchange.mutate().request(newRequest).build();
//            }
//        }
        return chain.filter(exchange);
    }
}
