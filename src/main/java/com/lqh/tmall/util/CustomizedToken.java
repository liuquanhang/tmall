package com.lqh.tmall.util;

import org.apache.shiro.authc.UsernamePasswordToken;
/**
*@author
*@date 20:44 2019/4/1
*@description 判断登陆类型
*/
public class CustomizedToken extends UsernamePasswordToken {

    private String loginType;

    public CustomizedToken(final String name,final String password,String loginType){
        super(name,password);
        this.loginType =loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
