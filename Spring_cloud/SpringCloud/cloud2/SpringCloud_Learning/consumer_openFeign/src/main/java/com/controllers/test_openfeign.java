package com.controllers;


import com.service.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test_openfeign {
    @Autowired
    private FeignService feignService;


    @GetMapping("/he")
    public String test(){
        return feignService.dd();
    }
}
