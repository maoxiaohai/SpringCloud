package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@RestController
public class orderController {
    public static final String PAYMENT_URL = "http://provider";
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/he")
    public String test(){
        return restTemplate.getForObject(PAYMENT_URL+"/test",String.class);
    }
    //链路追踪sleuth+zipkin测试
    @GetMapping("/consumer/payment/zipkin")
    public String paymentZipkin() {
        String result = restTemplate.getForObject(PAYMENT_URL + "/payment/zipkin", String.class);
        return result;
    }
}
