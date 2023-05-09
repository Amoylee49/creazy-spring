package com.shorturl;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@EnableDiscoveryClient
public class GateWayMain {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}