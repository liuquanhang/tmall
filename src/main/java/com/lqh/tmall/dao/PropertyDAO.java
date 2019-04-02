package com.lqh.tmall.dao;

import com.lqh.tmall.pojo.Category;
import com.lqh.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyDAO extends JpaRepository<Property, Integer> {
    //通过分类查询返回属性分页
    Page<Property> findByCategory(Category category, Pageable pageable);

    //通过分类查询属性
    List<Property> findByCategory(Category category);
}
