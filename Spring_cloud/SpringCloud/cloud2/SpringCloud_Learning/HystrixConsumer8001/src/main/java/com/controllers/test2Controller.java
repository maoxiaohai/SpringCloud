package com.controllers;

import com.services.Ibreak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test2Controller {

    @Autowired
    private Ibreak ibreak;

    @GetMapping("jj")
    public String dd(){
        return ibreak.test();
    }
}
