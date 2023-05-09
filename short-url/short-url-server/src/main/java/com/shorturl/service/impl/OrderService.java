package com.shorturl.service.impl;

import com.shorturl.mapper.OrderMapper;
import com.shorturl.vo.Order;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public int findOrder(int id) {
        Order order = orderMapper.findOrder(id);
        log.info("order==={}", order);
        return 1;
    }

}

@SpringBootTest
@Slf4j
class OrderTest{

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderService orderService;


    @Test
    public void testOrder(){
        Order order = orderMapper.selectById(2);
        log.info("order==={}", order);
    }

}