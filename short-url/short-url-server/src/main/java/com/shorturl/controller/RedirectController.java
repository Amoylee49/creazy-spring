package com.shorturl.controller;

import com.shorturl.exception.ApiException;
import com.shorturl.manage.ShortUrlManager;
import com.shorturl.vo.ShortUrlVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@Slf4j
@RequestMapping("/red")
public class RedirectController {

    @Autowired
    private ShortUrlManager urlManager;

    @GetMapping("/redirect/{hashValue}")
    public Mono<Void> redirect(ServerHttpResponse response, @PathVariable String hashValue){
        ShortUrlVO urlVO = urlManager.getRelUrlByHash(hashValue);
        if (null == urlVO){
            log.error("短链接不存在：hash-{}",hashValue);
            throw new ApiException("短链接不存在");
        }

        return Mono.fromRunnable(()->{
           response.setStatusCode(HttpStatus.FOUND);
           response.getHeaders().setLocation(URI.create(urlVO.getUrl()));
        });

    }
}
