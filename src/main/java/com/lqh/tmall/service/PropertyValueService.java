package com.lqh.tmall.service;

import com.lqh.tmall.dao.PropertyValueDAO;
import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.pojo.Property;
import com.lqh.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {

    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    @CacheEvict(allEntries = true)
    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }

    public void init(Product product) {    //初始化属性值对象，按产品和属性查询属性值是否为空，为空则初始化关联产品对象和属性对象，保存
        List<Property> propertyList = propertyService.listByCategory(product.getCategory());
        for (Property property : propertyList) {
            PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            if (null == propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    @Cacheable(key = "'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    @Cacheable(key = "'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }
}
