package com.shorturl.service.impl;

import com.shorturl.mapper.ShortUrlMapper;
import com.shorturl.service.ShortUrlService;
import com.shorturl.util.MathUtils;
import com.shorturl.util.RedisUtils;
import com.shorturl.vo.ShortUrlEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ShortUrlServiceImpl implements ShortUrlService {

    @Autowired
    private ShortUrlMapper urlMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String generateShortUrl(String url) {
        if (StringUtils.isBlank(url)){
            throw new RuntimeException("链接不存在");
        }
        int hash32x86 = MurmurHash3.hash32x86(url.getBytes());

        return MathUtils.switch_10_to_62(hash32x86);
    }

    @Override
    public int saveShortUrl(ShortUrlEntity entity) {

        int result = urlMapper.insert(entity);
        return result;
    }

    @Override
    public ShortUrlEntity findByHashValueFromDB(String hashValue) {
        ShortUrlEntity entity = urlMapper.findByHashValue(hashValue);
        return entity;
    }

    @Override
    @Cacheable(cacheNames = "shortUrl", key = "#hashValue")
    public ShortUrlEntity findUrlFromRedis(String hashValue) {
        ShortUrlEntity s = (ShortUrlEntity)redisUtils.getObj(hashValue);
        if (s != null)
            return s;
        return urlMapper.findByHashValue(hashValue);
    }

    @Override
    public List<String> findAllHashValueFromDB() {
        return urlMapper.findAllFromDB();
    }


    @Test
    public void testHash(){
        String url = "https://www.bjbdqnxx.com/?source=baidu&plan=beijing&unit=pinpai-tongyong&keyword=beidaqingniao&e_keywordid=590370904219&bd_vid=10596251342362551869";
        int x86 = MurmurHash3.hash32x86(url.getBytes());
        int f1 = MurmurHash3.hash32x86(url.getBytes());
        int f2 = MurmurHash3.hash32x86(url.getBytes());
        int f3 = MurmurHash3.hash32x86(url.getBytes());
        log.info("{},{},{},{}",x86,f1,f2,f3);
        log.info("转62进制：{}",MathUtils.switch_10_to_62(x86));
    }
}
