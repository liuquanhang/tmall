package com.lqh.tmall.es;

import com.lqh.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductESDAO extends ElasticsearchRepository<Product, Integer> {
}
