package com.dao;

import com.guigu.entities.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author GongXings
 * @createTime 30 14:48
 * @description
 */
@Mapper
public interface DeptDao {

    /**
     * 插入
     * @param deptEntity
     *
     * @return
     */
    boolean addDept(Dept deptEntity);

    /**
     * 根据id查找
     * @param deptNo
     * @return
     */
    Dept findById(Long deptNo);

    /**
     * 查询全部
     * @return
     */
    List<Dept> findAll();

}