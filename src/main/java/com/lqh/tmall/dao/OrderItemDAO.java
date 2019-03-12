package com.lqh.tmall.dao;
import com.lqh.tmall.pojo.Order;
import com.lqh.tmall.pojo.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
}
