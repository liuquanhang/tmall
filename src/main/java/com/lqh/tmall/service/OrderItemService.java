package com.lqh.tmall.service;

import com.lqh.tmall.dao.OrderItemDAO;
import com.lqh.tmall.pojo.Order;
import com.lqh.tmall.pojo.OrderItem;
import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void fill(List<Order> orders){
        for (Order order:orders) {
            fill(order);
        }
    }

    public void fill(Order order){
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem orderItem:orderItems) {
            total += orderItem.getNumber()*orderItem.getProduct().getPromotePrice();
            totalNumber += orderItem.getNumber();
            productImageService.setFirstProdutImage(orderItem.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
    }

    public List<OrderItem> listByOrder(Order order){
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    public List<OrderItem> listByProduct(Product product){
        return orderItemDAO.findByProduct(product);
    }

    public void update(OrderItem orderItem){
        orderItemDAO.save(orderItem);
    }

    public OrderItem get(int id){
        return orderItemDAO.findOne(id);
    }

    public void delete(int id){
        orderItemDAO.delete(id);
    }

    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    public int getSaleCount(Product product){
        List<OrderItem> list = listByProduct(product);
        int result = 0;
        for(OrderItem o:list){
            if(o.getOrder()!=null){
                if(o.getOrder()!=null&&o.getOrder().getPayDate()!=null){
                    result += o.getNumber();
                }
            }
        }
        return result;
    }

    public List<OrderItem> listByUser(User user){
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }
}
