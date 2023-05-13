package com.shorturl.controller;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.shorturl.api.CommonResult;
import com.shorturl.manage.ShortUrlManager;
import com.shorturl.util.RedisUtils;
import com.shorturl.vo.ShortUrlDto;
import com.shorturl.vo.ShortUrlVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/gen")
@RefreshScope
public class IndexController {
    @Autowired
    private ShortUrlManager shortUrlManager;

    @Autowired
    private RedisUtils redisUtils;

//    @Value("${domain:localhost}")
//    private final String DOMAIN = "localhost:8080/";

    @Value("${url.protocol:https}")
    private String https;
    @Value("${url.type:defaultValue}")
    private String type;

    @PostMapping(value = "/shortUrl", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> generateShortUrl(@RequestBody(required = false) ShortUrlDto request) {
        log.info("动态刷新参数：{}", type);
        if (!shortUrlManager.isValidUrl(request.getUrl())) {
            log.error("无效的url:[{}]", request.getUrl());
            throw new ApiException("无效的url");
        }
//        return CommonResult.success(DOMAIN + shortUrlManager.generateShortUrl(request.getUrl()).getHashValue());
        return CommonResult.success(shortUrlManager.generateShortUrl(request.getUrl()).getHashValue());
    }

    @PostMapping(value = "/check", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus
    public boolean check(@RequestBody(required = false) ShortUrlVO vo) {
        log.info("{}", vo);
        return true;
    }

    @GetMapping("/getByHash/{hashValue}")
    public CommonResult<String> getByHash(@PathVariable("hashValue") String hash) {
        log.info("====================请求hash:[{}]===============", hash);
        ShortUrlVO shortUrlVO = shortUrlManager.getRelUrlByHash(hash);
        if (null == shortUrlVO) {
            log.error("短链接不存在,hash[{}]", hash);
            throw new ApiException("短链接不存在");
        }
        return CommonResult.success(shortUrlVO.getUrl());
    }

}
