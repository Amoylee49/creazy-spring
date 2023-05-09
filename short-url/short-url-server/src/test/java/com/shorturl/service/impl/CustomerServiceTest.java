package com.shorturl.service.impl;

import com.shorturl.StartApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { StartApplication.class})
class CustomerServiceTest {

    static Object lock = new Object();

    @Test
    public void run() throws InterruptedException {
        // 无限等待,使得jvm不会退出
        while(true){
            synchronized(lock){
                // 除非有线程唤醒他 lock.notify();
                lock.wait();
            }
        }
    }

    @Autowired
    private CustomerService customerService;

    @Autowired
    OrderService orderService;

    @Test
    public void testC(){
        customerService.findCustomer();
    }
     @Test
    public void testO(){
        orderService.findOrder(1);
    }


}