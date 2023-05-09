package com.shorturl.filter;

import com.google.common.base.Preconditions;
import com.shorturl.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisBloomFilter {
    @Autowired
    private RedisUtils redisUtils;

    //根据给定布隆过滤器添加值
    public <T> void addByBloomFilter(BloomFilterHelper<T> helper, String key, T value){
        Preconditions.checkArgument(helper!= null,"helper 不能为空");
        int[] ints = helper.murmurHashOffset(value);
        for (int i : ints) {
            System.out.println("key : " + key + " " + "value : " + i);
            redisUtils.setBit(key,i,true);
        }
    }
    /**
     * 根据给定的布隆过滤器判断值是否存在
     */
    public <T> boolean includeByBloomFilter(BloomFilterHelper helper, String key, T value){
        Preconditions.checkArgument(helper!= null,"helper 不能为空");
        int[] ints = helper.murmurHashOffset(value);
        for (int i : ints) {
            System.out.println("key : " + key + " " + "value : " + i);
            if (!redisUtils.getBit(key, i)) {
                return false;
            }
        }
        return true;
    }
}
