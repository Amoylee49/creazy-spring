package com.shorturl.mapper;

import com.shorturl.service.impl.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;


    @Test
    public void testOrder(){
        orderService.findOrder(2);
    }


}