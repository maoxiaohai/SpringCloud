package com.controllers;


import com.services.IMessageProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ProviderController {
    @Resource
    private IMessageProvider messageProvider;
    @GetMapping("/sendMessage")
    public String send(){
        return messageProvider.send();
    }
}
