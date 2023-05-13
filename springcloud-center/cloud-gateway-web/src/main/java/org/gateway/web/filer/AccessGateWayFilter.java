package org.gateway.web.filer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.auth.client.service.IAuthService;
import org.gateway.web.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 请求url 权限校验
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "org.auth.client")
public class AccessGateWayFilter implements GlobalFilter {

    private static final String X_CLIENT_TOKEN_USER = "x-client-token-user";
    private static final String X_CLIENT_TOKEN = "x-client-token";
    @Autowired
    private IAuthService authService;

    @Autowired
    private IPermissionService permissionService;

    @Override
    /**
    * 1.首先网关检查token是否有效，无效直接返回401，不调用签权服务
    * 2.调用签权服务器看是否对该请求有权限，有权限进入下一个filter，没有权限返回401
    */
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String method = request.getMethodValue();
        String url = request.getPath().value();
        log.debug("url:{},method:{},headers:{}", url, method, request.getHeaders());
        //不需要网关鉴权的url
        if (authService.ignoreAuthentication(url))
            return chain.filter(exchange);
        //调用鉴权服务看用户是否有权限，若有权限进入下一个filter
        if (permissionService.permission(authorization,url,method)){
            ServerHttpRequest.Builder builder = request.mutate();
            builder.header(X_CLIENT_TOKEN,"TODO 添加服务间简单认证");

            //添加jwt token 用户信息传给服务
            builder.header(X_CLIENT_TOKEN_USER, getUserToken(authorization));
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

        return unauthorized(exchange);
    }

    //提取jwt token 中的内容 转为json JWT由三部分组成，分别是：头部，载荷和签名 header、payload、secret（保密）
    private String getUserToken(String authentication) {
        String token = "{}";

        try {
            token = JSON.toJSONString(authService.JwtParseToken(authentication).getPayload());
            return token;
        } catch (Exception e) {
            log.error("token json error:{}",e.getMessage());
        }
        return token;
    }

    /**
     * 网关拒绝，返回401
     *
     * @param
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }


}
