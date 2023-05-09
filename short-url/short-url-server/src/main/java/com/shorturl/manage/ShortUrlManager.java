package com.shorturl.manage;

import com.shorturl.exception.ApiException;
import com.shorturl.filter.BloomFilterHelper;
import com.shorturl.filter.RedisBloomFilter;
import com.shorturl.service.ShortUrlService;
import com.shorturl.util.RedisUtils;
import com.shorturl.vo.ShortUrlEntity;
import com.shorturl.vo.ShortUrlVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RefreshScope //动态刷新
public class ShortUrlManager {
    @Autowired
    private ShortUrlService shortUrlService;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisBloomFilter redisBloomFilter;
    @Autowired
    private BloomFilterHelper bloomHelper;

    public static final String URL_DUPLICATED = "[DUPLICATED]";
    @Value("${url.protocol:https}")
    private String url_prefix;

    public ShortUrlVO generateShortUrl(String url) {
        //判断url  http https 头
        if (StringUtils.isBlank(url))
            throw new ApiException("参数错误");
        url = StringUtils.trim(url);
        if (!isStartWithHttpsOrHttp(url))
            url = appendHttpsHead(url, url_prefix);
        //这里多台机器可能出现并发问题，查询->插入，可能会出现问题，但是有数据库唯一索引保护
        String urlHashCode = shortUrlService.generateShortUrl(url);
//计算多差次拼接才能生成不重复的 hash value
        int count = 0;
        while (true) {
            if (count > 5)
                throw new ApiException("重试拼接url 超过限制次数");
            //从bloomfilter 查看是否存在
            boolean isContain = redisBloomFilter.includeByBloomFilter(bloomHelper, "bloom", urlHashCode);
            if (!isContain){
                redisBloomFilter.addByBloomFilter(bloomHelper,"bloom",urlHashCode);
                log.info("============生成短链接，判断短链接不存在,可以生成对应关系!===============");
                break;
            }

            //hash 相同且长连接相同
            ShortUrlEntity dbShortUrl = shortUrlService.findUrlFromRedis(urlHashCode);
            if (urlHashCode.equals(dbShortUrl.getHashValue())){
                log.info("============= 短链接已经存在 ===============");
                return new ShortUrlVO(dbShortUrl.getHashValue(), dbShortUrl.getUrl());
            }else{
                log.warn("=========== hashValue :[{}], dbUrl:[{}], currentUrl[{}]",
                        urlHashCode,dbShortUrl.getUrl(),url);
                    url += URL_DUPLICATED;
                    urlHashCode = shortUrlService.generateShortUrl(url);
                log.warn("========= url 重新拼接hash：【{}】,currentUrl:[{}]",urlHashCode,url);
            }
            count++;
            log.info("========= url 重新拼接字符串，次数：【{}】",count);
        }

        //入库
        ShortUrlEntity urlEntity = ShortUrlEntity.builder()
                .hashValue(urlHashCode)
                .url(url).build();
        try {
            shortUrlService.saveShortUrl(urlEntity);
            redisUtils.setEx(urlHashCode,urlEntity,12, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("重复插入问题：{}",e);
            throw new ApiException("重复插入url");
        }
        return new ShortUrlVO(urlEntity.getHashValue(),urlEntity.getUrl());
    }

    private static boolean isStartWithHttpsOrHttp(String url) {
        String regex = "^((https|http)?://)";
        Pattern matches = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Matcher matcher = matches.matcher(url);
        return matcher.find();
    }

    //url 开头拼https
    private String appendHttpsHead(String url, String url_prefix) {
        StringBuilder strb = new StringBuilder(url_prefix).append("://");
        strb.append(url);
        return strb.toString();
    }

    /**
     * 是否是有效的 url
     *
     * @param urls
     * @return
     */
    public boolean isValidUrl(String urls) {
        //设置正则表达式
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        //对比
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(urls.trim());
        //判断是否匹配
        return mat.matches();
    }

    @Test
    public void test() {
        String url = "https://www.baidu.com";
        boolean startWithHttpsOrHttp = isStartWithHttpsOrHttp(url);
        System.out.println(startWithHttpsOrHttp);
        if (!startWithHttpsOrHttp)
            url = appendHttpsHead(url, url_prefix);
//            throw new ApiException("不是http 头");
//        String httpsHead = appendHttpsHead(url, "https");
        System.out.println(url);
    }

    public ShortUrlVO getRelUrlByHash(String hashValue) {

        ShortUrlEntity fromDB = shortUrlService.findByHashValueFromDB(hashValue);
        if (null == fromDB)
            return null;
        String replace = fromDB.getUrl().replace(URL_DUPLICATED, "");
        return new ShortUrlVO(fromDB.getHashValue(),replace);
    }
}
