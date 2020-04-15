package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
@EnableEurekaClient
public class hystrix8001 {
    public static void main(String[] args) {
        SpringApplication.run(hystrix8001.class,args);
    }
}
