package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient  //Eureca客户端使能注解。可能是ribbon和eureca之间维护服务表的作用
public class consumer_80Application {
    public static void main(String[] args) {
        SpringApplication.run(consumer_80Application.class, args);
    }

}
