package com.shorturl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shorturl.vo.Customer;
import com.shorturl.vo.Order;

public interface CustomerMapper extends BaseMapper<Order> {

    Customer findCustomer(int id);
}
