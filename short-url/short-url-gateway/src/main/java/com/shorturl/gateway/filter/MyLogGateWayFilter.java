package com.shorturl.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MyLogGateWayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //Todo 可以在这里做拦截，实现参数验证,权限验证，授权等操作
        log.info("进入 gateway filter 了++++++++++++++++++{}",exchange);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
