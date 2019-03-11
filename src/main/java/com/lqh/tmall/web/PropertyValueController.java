package com.lqh.tmall.web;

import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.pojo.PropertyValue;
import com.lqh.tmall.service.ProductService;
import com.lqh.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid")int pid){
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }

    @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue bean){
        propertyValueService.update(bean);
        return bean;
    }
}
