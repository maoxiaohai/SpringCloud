package com.services;

import com.config.restConfig;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class breakService {
    @Autowired
    private RestTemplate restTemplate;

    //访问服务时间过长时，调用callback
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler", commandProperties = {
            //超时5s或者异常直接降级
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    public String time_out(Integer id){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return restTemplate.getForObject("http://provider/test",String.class);
    }
    public  String paymentInfo_TimeOutHandler(@PathVariable("id") Integer id){
        return "yanshi";
    }

   //服务发生异常时，调用callback
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "false"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "50000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),// 失败率达到多少后跳闸
    })
    public String circit(Integer id){
        if(id<0)
            throw new RuntimeException("ID不能小于0");
        return restTemplate.getForObject("http://provider/test",String.class);
    }
    public String paymentCircuitBreaker_fallback(@PathVariable("id") Integer id){

        return "服务断线!";
    }
}
