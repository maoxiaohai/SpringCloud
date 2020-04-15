package com.controller;


import com.guigu.entities.Dept;
import com.guigu.service.DeptClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class DeptController_Consumer {
    @Autowired
    private DeptClientService deptClientService;
    @RequestMapping(value = "/consumer/dept/add")
    public boolean add( Dept Dept){
        //三个参数：url,requestMap ResponseBean.class
        return  deptClientService.add(Dept);
    }
    @RequestMapping("/consumer/dept/findById/{deptNo}")
    public Dept findById(@PathVariable("deptNo") Long deptNo){
        //三个参数：url,requestMap ResponseBean.class
        return deptClientService.findById(deptNo);
    }
    @RequestMapping("/consumer/dept/findAll")
    public List findAll(){
        //三个参数：url,requestMap ResponseBean.class
        return  deptClientService.findAll();
    }
}
