package com.lqh.tmall;

import com.lqh.tmall.util.PortUtil;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching  //添加缓存
@EnableElasticsearchRepositories(basePackages = "com.lqh.tmall.es")  //添加Elasticsearch搜索
@EnableJpaRepositories(basePackages = {"com.lqh.tmall.dao", "com.lqh.tmall.pojo"})  //添加JPA
public class Application {
    static {
        PortUtil.checkPort(6379, "Redis服务端", true);
        PortUtil.checkPort(9300, "ElasticSearch服务端", true);
        PortUtil.checkPort(5601, "Kibana工具", true);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
