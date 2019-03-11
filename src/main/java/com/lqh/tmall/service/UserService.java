package com.lqh.tmall.service;

import com.lqh.tmall.dao.UserDAO;
import com.lqh.tmall.pojo.User;
import com.lqh.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    public Page4Navigator<User> list(int start,int size,int navigatePages){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<User> users = userDAO.findAll(pageable);
        return new Page4Navigator<>(users,navigatePages);
    }
}
