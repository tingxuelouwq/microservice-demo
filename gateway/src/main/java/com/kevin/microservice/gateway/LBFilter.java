package com.kevin.microservice.gateway;

/**
 * kevin<br/>
 * 2024/6/17 15:32<br/>
 */
//@Component
//public class LBFilter implements GlobalFilter {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//        if (request.getQueryParams().getFirst("gray-tag") != null) {
//            response.getHeaders().set("gray-tag", "true");
//        }
//        return chain.filter(exchange);
//    }
//}
