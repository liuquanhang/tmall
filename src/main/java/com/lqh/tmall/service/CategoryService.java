package com.lqh.tmall.service;

import com.lqh.tmall.dao.CategoryDAO;
import com.lqh.tmall.pojo.Category;
import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    @Cacheable(key = "'categories-all'")
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    @Cacheable(key = "'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    @CacheEvict(allEntries = true)
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        categoryDAO.delete(id);
    }

    @Cacheable(key = "'categories-one-'+ #p0")
    public Category get(int id) {
        Category c = categoryDAO.findOne(id);
        return c;
    }

    @CacheEvict(allEntries = true)
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    //删除产品中的分类属性，防止转为Json时无限遍历
    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
