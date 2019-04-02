package com.lqh.tmall.dao;

import com.lqh.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
/**
*@author
*@date 9:40 2019/3/30
*@description 管理员DAO
*/
public interface UserDAO extends JpaRepository<User, Integer> {
    User findByName(String name);

    User getByNameAndPassword(String name, String password);
}
