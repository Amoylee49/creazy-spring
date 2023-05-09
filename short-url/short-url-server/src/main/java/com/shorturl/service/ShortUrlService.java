package com.shorturl.service;

import com.shorturl.vo.ShortUrlEntity;

import java.util.List;

public interface ShortUrlService {

    //将长链接 转成短链接
    String generateShortUrl(String url);

    //保存到数据库
    int saveShortUrl( ShortUrlEntity entity);

    //从数据库查询
    ShortUrlEntity findByHashValueFromDB(String hashValue);

    //从本地缓存查出来
    ShortUrlEntity findUrlFromRedis(String havaValue);

    //查询db所有hashValue
    List<String> findAllHashValueFromDB();

}
