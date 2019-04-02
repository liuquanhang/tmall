package com.lqh.tmall.dao;

import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDAO extends JpaRepository<Review, Integer> {
    List<Review> findByProductOrderByIdDesc(Product product);  //查询某产品对应的评价集合

    int countByProduct(Product product);  //查询某产品对应的评价数量

}
