package com.lqh.tmall.dao;

import com.lqh.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


public interface CategoryDAO extends JpaRepository<Category, Integer> {

}
