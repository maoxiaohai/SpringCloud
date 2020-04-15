package com.services;

import org.springframework.stereotype.Component;

@Component
public class breakImpl implements Ibreak {
    @Override
    public String test() {
        return "error Code";
    }
}
