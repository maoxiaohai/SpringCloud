package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class provider8801 {
    public static void main(String[] args) {
        SpringApplication.run(provider8801.class,args);
    }
}
