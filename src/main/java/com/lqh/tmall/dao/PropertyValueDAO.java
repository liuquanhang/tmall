package com.lqh.tmall.dao;

import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.pojo.Property;
import com.lqh.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
    //根据产品查询
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    //根据产品和属性查询
    PropertyValue getByPropertyAndProduct(Property property, Product product);
}
