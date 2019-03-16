package com.lqh.tmall.interceptor;

import com.lqh.tmall.pojo.Category;
import com.lqh.tmall.pojo.OrderItem;
import com.lqh.tmall.pojo.User;
import com.lqh.tmall.service.CategoryService;
import com.lqh.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class OtherInterceptor implements HandlerInterceptor {
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");
        int cartTotalItemNumber = 0;
        if(user!=null){
            List<OrderItem> ois = orderItemService.listByUser(user);
            for(OrderItem orderItem:ois){
                cartTotalItemNumber+=orderItem.getNumber();
            }
        }
        List<Category> cs = categoryService.list();
        String contextPath = httpServletRequest.getServletContext().getContextPath();
        httpServletRequest.getServletContext().setAttribute("categories_below_search",cs);
        session.setAttribute("cartTotalNumber",cartTotalItemNumber);   //购物车数量保存在session中
        httpServletRequest.getServletContext().setAttribute("contextPath",contextPath);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
