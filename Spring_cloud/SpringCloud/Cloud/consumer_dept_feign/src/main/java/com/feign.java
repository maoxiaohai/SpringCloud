package com;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
/**
 * 在启动该微服务式是能去加载我们定义的Feign配置类
 */
@EnableFeignClients
//@ComponentScan("com")
@SpringBootApplication
public class feign {
    public static void main(String[] args) {
        SpringApplication.run(feign.class, args);
    }

}
