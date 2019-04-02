package com.lqh.tmall.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
*@author
*@date 12:24 2019/3/30
*@description 后台管理员登陆拦截器
*/
public class BackgroundInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        String contextPath = session.getServletContext().getContextPath();
        String[] requireAuthPages = new String[]{
            "admin_category_list",
            "admin_category_edit",
            "admin_order_list",
                "admin_product_list",
                "admin_product_edit",
                "admin_productImage_list",
                "admin_property_list",
                "admin_property_edit",
                "admin_propertyValue_edit",
                "admin_user_list"
        };
        String uri = httpServletRequest.getRequestURI();
        uri = StringUtils.remove(uri,contextPath+"/");
        if(beginWith(uri,requireAuthPages)){
            Subject subject = SecurityUtils.getSubject();
            if(!subject.isAuthenticated()){
                httpServletResponse.sendRedirect("admin_login");
                return false;
            }
        }
        return true;
    }

    private boolean beginWith(String uri,String[] requireAuthPages){
        boolean result = false;
        for (String page:requireAuthPages){
            if (StringUtils.startsWith(uri,page)){
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
