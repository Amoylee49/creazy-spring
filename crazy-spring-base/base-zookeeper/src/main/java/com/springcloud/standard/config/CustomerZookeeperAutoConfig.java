package com.springcloud.standard.config;

import com.springcloud.standard.distrubute.zookeeper.ZKClient;
import com.springcloud.standard.hibernate.SnowflakeIdGeneratorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ConditionalOnProperty(prefix = "zookeeper",name = "address")
public class CustomerZookeeperAutoConfig {
    @Value("${zookeeper.address}")
    private String zkAddress;

    /**
     * 自定义的ZK客户端bean
     *
     * @return
     */
    @Bean(name = "zKClient" )
    public ZKClient zKClient()
    {
        return new ZKClient(zkAddress);
    }

    /**
     * 获取 ZK 限流器的 bean
     */
//    @Bean
//    @DependsOn("zKClient" )
//    public RateLimitService zkRateLimitServiceImpl()
//    {
//        return new ZkRateLimitServiceImpl();
//    }
//
//    /**
//     * 获取 ZK 分布式锁的 bean
//     */
//
//    @Bean
//    @DependsOn("zKClient" )
//    public LockService zkLockServiceImpl()
//    {
//        return new ZkLockServiceImpl();
//    }


    /**
     * 获取通用的分布式ID 生成器 工程
     */
    @Bean
    @DependsOn("zKClient" )
    public SnowflakeIdGeneratorFactory snowflakeIdGeneratorFactory()
    {
        return new SnowflakeIdGeneratorFactory();
    }
}
