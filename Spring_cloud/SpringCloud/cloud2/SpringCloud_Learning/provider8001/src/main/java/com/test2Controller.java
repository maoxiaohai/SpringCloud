package com;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test2Controller {
    @GetMapping("/test")
    public String tst(){
        return "hello World 8001";
    }
}
