package com.lqh.tmall.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


//接受从管理员后台页面发送信息的后端处理器
@Controller
public class AdminPageController {

    @GetMapping(value = "/admin")
    public String admin() {
        //重定向到listCategory方法，重定向不会通过视图解析器
        return "redirect:admin_category_list";
    }

    @GetMapping(value = "/admin_category_list")
    public String listCategory() {
        return "admin/listCategory";
    }

    @GetMapping(value = "/admin_category_edit")
    public String editCategory() {
        return "admin/editCategory";
    }

    @GetMapping(value = "/admin_order_list")
    public String listOrder() {
        return "admin/listOrder";
    }

    @GetMapping(value = "/admin_product_list")
    public String listProduct() {
        return "admin/listProduct";
    }

    @GetMapping(value = "/admin_product_edit")
    public String editProduct() {
        return "admin/editProduct";
    }

    @GetMapping(value = "/admin_productImage_list")
    public String listProductImage() {
        return "admin/listProductImage";
    }

    @GetMapping(value = "/admin_property_list")  //转发'admin_property_list?cid=' + bean.id 到listProperty.html
    public String listProperty() {
        return "admin/listProperty";
    }

    @GetMapping(value = "/admin_property_edit")
    public String editProperty() {
        return "admin/editProperty";
    }

    @GetMapping(value = "/admin_propertyValue_edit")
    public String editPropertyValue() {
        return "admin/editPropertyValue";
    }

    @GetMapping(value = "/admin_user_list")
    public String listUser() {
        return "admin/listUser";
    }

    @GetMapping(value = "/admin_login")
    public String login(){
        return "admin/login";
    }
}

