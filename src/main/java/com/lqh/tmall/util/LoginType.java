package com.lqh.tmall.util;


public enum LoginType {
    /**
     * 用户类型登录
     */
    USER("User"),
    /**
     * 管理员类型登陆
     */
    ADMIN("Admin");

    private String type;

    private LoginType(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
