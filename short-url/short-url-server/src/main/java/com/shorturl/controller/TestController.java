package com.shorturl.controller;

import com.shorturl.service.ShortUrlService;
import com.shorturl.vo.ShortUrlEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ShortUrlService urlService;

    @RequestMapping("/insert")
    public int insertTest(@RequestParam(required = false) long id){
        ShortUrlEntity entity = ShortUrlEntity.builder()
                .id(id)
                .url("www.baidu.com")
                .hashValue("OUSOFUSDF")
                .deleted((byte) 0)
                .build();
        log.info("entity是多少：{}",entity);
        int i = urlService.saveShortUrl(entity);

        return i;
    }


}
