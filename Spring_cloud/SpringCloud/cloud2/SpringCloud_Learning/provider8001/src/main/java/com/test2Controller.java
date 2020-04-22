package com;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test2Controller {
    @GetMapping("/test")
    public String tst(){
        return "hello World 8001";
    }
    //链路追踪sleuth+zipkin测试
    @GetMapping("/payment/zipkin")
    public String paymentZipkin() {
        return "paymentzipkin调用成功~ ，O(∩_∩)O哈哈~";
    }
}
