package com.lqh.tmall.service;

import com.lqh.tmall.dao.AdministratorDAO;
import com.lqh.tmall.pojo.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "admins")
public class AdministratorService {

    private final AdministratorDAO administratorDAO;

    @Autowired
    public AdministratorService(AdministratorDAO administratorDAO){
        this.administratorDAO = administratorDAO;
    }

    public boolean isExist(String name){
        Administrator adm = administratorDAO.findByName(name);
        return adm != null;
    }

    @Cacheable(key = "'admin-name-'+#p0")
    public Administrator getByName(String name){
        Administrator adm = administratorDAO.findByName(name);
        return adm;
    }

    @Cacheable(key = "'admin-name-'+#p0+'admin-password-'+#p0")
    public boolean get(String name,String password){
        Administrator adm = administratorDAO.getByNameAndPassword(name, password);
        return adm != null;
    }

    public void update(Administrator administrator){
        administratorDAO.save(administrator);
    }
}
