package com.springcloud.eureka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@EnableEurekaServer
@SpringBootApplication
@Slf4j
public class EurekaServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EurekaServerApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
//        String path = env.getProperty("server.servlet.context-path");
        String ip = env.getProperty("eureka.instance.ip-address");

        log.info("\n----------------------------------------------------------\n\t" +
                "Eureka 注册中心 is running! Access URLs:\n\t" +
                "Local: \t\thttp://" + ip + ":" + port + "/\n\t" +
                "actuator: \thttp://" + ip + ":" + port + "/actuator/\n\t" +
                "----------------------------------------------------------");
    }

}