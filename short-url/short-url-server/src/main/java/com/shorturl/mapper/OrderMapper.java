package com.shorturl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shorturl.vo.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper extends BaseMapper<Order> {

    Order findOrder(@Param("id") int id);
}
