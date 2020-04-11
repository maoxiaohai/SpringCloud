package com.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
@Api(tags = "ceshi")
public class testController {
    @ApiOperation(value = "查询")
    @GetMapping("list")
    public String get(){
        return "list";
    }
    @ApiOperation(value = "添加")
    @GetMapping("add")
    public String add(){
        return "add";
    }
    @ApiOperation(value = "删除")
    @GetMapping("delete")
    public String delete(){
        return "delete";
    }

}
