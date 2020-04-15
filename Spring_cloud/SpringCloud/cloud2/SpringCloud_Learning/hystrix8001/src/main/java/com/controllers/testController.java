package com.controllers;

import com.services.breakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {
    @Autowired
    private breakService breakService;

    @GetMapping("/timeout/{id}")
    public  String time_out(@PathVariable("id")Integer id){
        return breakService.time_out(id);
    }

    @GetMapping("/cir/{id}")
    public String circit(@PathVariable("id")Integer id){
        return breakService.circit(id);
    }
}
