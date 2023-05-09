package com.shorturl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shorturl.vo.ShortUrlEntity;

import java.util.List;

public interface ShortUrlMapper extends BaseMapper<ShortUrlEntity> {

    ShortUrlEntity findByHashValue(String hashValue);

//    int insert(String url);

    List<String> findAllFromDB();
}
