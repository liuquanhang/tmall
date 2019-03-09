package com.lqh.tmall.service;

import com.lqh.tmall.dao.CategoryDAO;
import com.lqh.tmall.pojo.Category;
import com.lqh.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;
    public List<Category> list(){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        return categoryDAO.findAll(sort);
    }
    public Page4Navigator<Category> list(int start,int size,int navigatePages){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }
    public void add(Category bean){
        categoryDAO.save(bean);
    }
    public void delete(int id){
        categoryDAO.delete(id);
    }
    public Category get(int id){
        Category c = categoryDAO.findOne(id);
        return c;
    }
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

}
