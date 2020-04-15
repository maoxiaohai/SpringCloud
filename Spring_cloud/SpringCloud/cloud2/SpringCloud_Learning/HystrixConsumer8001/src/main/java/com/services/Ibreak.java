package com.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;


@Component
@FeignClient(value = "provider",fallback = breakImpl.class)
public interface Ibreak {
    @GetMapping("/test")
    public String test();
}
