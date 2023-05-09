package com.springcloud.zuul;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication(
        scanBasePackages = {
                "org.springcloud.zuul",
        },
        exclude = {
//                SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class,
//                DataSourceAutoConfiguration.class,
//                HibernateJpaAutoConfiguration.class,
//                DruidDataSourceAutoConfigure.class,
//                RedisSessionFilterConfig.class
        }
)
@EnableScheduling
@EnableHystrix
@EnableDiscoveryClient
//声明一个zuul服务
@EnableZuulProxy
public class ZuulServerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ZuulServerApplication.class, args);
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String name = env.getProperty("spring.application.name");

        String path = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(path)) {
            path = "";
        }

        log.info("++++++++++total env ++++++++++++++:{}", env);
        String ip = env.getProperty("spring.cloud.client.ip-address");

        log.info("\n----------------------------------------------------------\n\t" +
                name.toUpperCase() + " is running! Access URLs:\n\t" +
                "Local: \t\thttp://" + ip + ":" + port + path + "/\n\t" +
                "IP without port: \t\thttp://" + ip + ":" + "\n\t" +
                "swagger-ui: \thttp://" + ip + ":" + port + "swagger-ui.html\n\t" +
                "actuator: \thttp://" + ip + ":" + port + "/actuator/info\n\t" +
                "----------------------------------------------------------");
    }

}