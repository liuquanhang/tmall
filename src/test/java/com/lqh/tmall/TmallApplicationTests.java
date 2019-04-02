package com.lqh.tmall;

import com.lqh.tmall.pojo.Administrator;
import com.lqh.tmall.service.AdministratorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmallApplicationTests {

    @Autowired
    AdministratorService administratorService;

    @Test
    public void test01() {
        Administrator admin01 = administratorService.getByName("admin01");
        System.out.println(admin01);
    }

    @Test
    public void test02(){
        boolean admin01 = administratorService.isExist("admin01");
        System.out.println(admin01);
    }

    @Test
    public void test03(){
        String name ="admin01";
        String password = "123456";
        boolean b = administratorService.get(name, password);
        System.out.println(b);
    }

    @Test
    public void test04(){
        String name = "admin01";
        Administrator admin = administratorService.getByName(name);
        System.out.println(admin);
    }

    @Test
    public void test05(){
        Administrator admin02 = administratorService.getByName("admin02");
        admin02.setPassword("666666");
        administratorService.update(admin02);
    }
}
