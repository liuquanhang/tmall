package com.lqh.tmall.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//接受从管理员后台页面发送信息的后端处理器
@Controller
public class AdminPageController {

    @GetMapping(value= "/admin")
    public String admin(){
        return "redirect:admin_category_list"; //重定向到listCategory方法，重定向不会通过视图解析器
    }

    @GetMapping(value="/admin_category_list")
    public String listCategory(){
        return "admin/listCategory";
    }

    @GetMapping(value = "/admin_category_edit") //接受前台编辑分类信息
    public String editCategory(){
        return "admin/editCategory";
    }
}

