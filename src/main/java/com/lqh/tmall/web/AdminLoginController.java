package com.lqh.tmall.web;

import com.lqh.tmall.pojo.Administrator;
import com.lqh.tmall.service.AdministratorService;
import com.lqh.tmall.util.CustomizedToken;
import com.lqh.tmall.util.LoginType;
import com.lqh.tmall.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpSession;

/**
*@author
*@date 9:35 2019/3/31
*@description 后台管理员登陆处理器
*/
@RestController
public class AdminLoginController {
    //登陆用户类型
    private static final String ADMIN_LOGIN_TYPE = LoginType.ADMIN.toString();

    private final AdministratorService administratorService;

    @Autowired
    public AdminLoginController(AdministratorService administratorService){
        this.administratorService = administratorService;
    }

    @PostMapping("/adminlogin")
    public Object login(@RequestBody Administrator administrator, HttpSession session){
        String name = administrator.getName();
        String password = administrator.getPassword();
        Subject currentUser = SecurityUtils.getSubject();
        if(!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new CustomizedToken(name,password,ADMIN_LOGIN_TYPE);
            try {
                currentUser.login(token);
                Administrator admin = administratorService.getByName(name);
                session.setAttribute("admin", admin);
                return Result.success();
            } catch (IncorrectCredentialsException e) {
                return Result.fail("密码错误");
            }catch (LockedAccountException e){
                return Result.fail("账号已被冻结");
            }
        }
        return Result.success("你已成功");
    }

    //退出登录
    @GetMapping("/adminlogout")
    public String logout(){
        Subject currentAdmin = SecurityUtils.getSubject();
        if(currentAdmin.isAuthenticated()){
            currentAdmin.logout();
        }
        return "redirect:admin/login";
    }
}
