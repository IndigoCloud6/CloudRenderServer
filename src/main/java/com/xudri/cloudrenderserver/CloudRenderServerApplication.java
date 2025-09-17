package com.xudri.cloudrenderserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 虚幻引擎像素流送信令服务器主应用类
 * 
 * @author MaxYun
 * @version 1.0
 * @since 2024-01-01
 */
@EnableAsync
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
@MapperScan("com.xudri.cloudrenderserver.infrastructure.repository")
public class CloudRenderServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudRenderServerApplication.class, args);
    }

}
