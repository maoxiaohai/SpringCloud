package com.guigu.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Dept implements Serializable{
    /**
     *  部门编码 主键
     */
    private Long deptNo;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 数据库名称，因为微服务架构可以一个服务对应一个数据库，同一个信息被存储到不同数据库
     */
    private String dbSource;
}
