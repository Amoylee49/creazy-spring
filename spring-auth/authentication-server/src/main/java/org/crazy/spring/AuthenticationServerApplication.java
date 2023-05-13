package org.crazy.spring;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableMethodCache(basePackages = "com.springboot.cloud")
@EnableCreateCacheAnnotation
public class AuthenticationServerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthenticationServerApplication.class)
//                .bannerMode(Banner.Mode.OFF)
//                .web(WebApplicationType.REACTIVE)
                .build()
                .run(args);
    }
}