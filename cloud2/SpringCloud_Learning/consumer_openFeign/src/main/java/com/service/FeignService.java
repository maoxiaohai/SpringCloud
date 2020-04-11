package com.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/*
* 远程调用接口
* 在Ribbon中是使用的restTemplate进行调用。
* Feign中支持使用接口调用
* * */
@Component
@FeignClient(value = "provider")
public interface FeignService {
    @GetMapping("/test")
    public String dd();
}
