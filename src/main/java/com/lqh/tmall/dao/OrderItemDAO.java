package com.lqh.tmall.dao;
import com.lqh.tmall.pojo.Order;
import com.lqh.tmall.pojo.OrderItem;
import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    List<OrderItem> findByProduct(Product product);
    List<OrderItem> findByUserAndOrderIsNull(User user);
}
