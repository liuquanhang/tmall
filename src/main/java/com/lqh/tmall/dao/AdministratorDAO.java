package com.lqh.tmall.dao;

import com.lqh.tmall.pojo.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorDAO extends JpaRepository<Administrator,Integer> {

    Administrator findByName(String name);

    Administrator getByNameAndPassword(String name,String password);


}
