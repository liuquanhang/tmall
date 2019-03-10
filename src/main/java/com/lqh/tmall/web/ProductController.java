package com.lqh.tmall.web;

import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.service.CategoryService;
import com.lqh.tmall.service.ProductService;
import com.lqh.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;

    @GetMapping("categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid, @RequestParam(value = "start",defaultValue = "0")int start,@RequestParam(value = "size",defaultValue = "5")int size){
        start = start>0?0:start;
        Page4Navigator<Product> page = productService.list(cid, start, size, 5);
        return page;
    }
    @GetMapping("/products/{id}")
    public Product get(@PathVariable("id") int id){   //通过id获取product
        Product bean=productService.get(id);
        return bean;
    }

    @PostMapping("/products")
    public Object add(@RequestBody Product bean) {   //保存新增的product
        bean.setCreateDate(new Date());
        productService.add(bean);
        return bean;
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)  {  //通过id删除product
        productService.delete(id);
        return null;
    }

    @PutMapping("/products")
    public Object update(@RequestBody Product bean){  //更新product信息
        productService.update(bean);
        return bean;
    }
}
