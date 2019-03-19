package com.lqh.tmall.service;

import com.lqh.tmall.dao.ProductDAO;
import com.lqh.tmall.es.ProductESDAO;
import com.lqh.tmall.pojo.Category;
import com.lqh.tmall.pojo.Product;
import com.lqh.tmall.util.Page4Navigator;
import com.lqh.tmall.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProductESDAO productESDAO;

    @CacheEvict(allEntries=true)
    public void add(Product bean) throws Exception{
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id) {
        productDAO.delete(id);
        productESDAO.delete(id);
    }

    @Cacheable(key = "'products-one-'+#p0")
    public Product get(int id) {
        return productDAO.findOne(id);
    }

    @CacheEvict(allEntries=true)
    public void update(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    //分页展示产品
    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int id,int start,int size,int navigatePages){
        Category category = categoryService.get(id);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page<Product> pageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }
    //为多个分类填充产品集合
    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }
    //为分类填充产品集合
    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    //查询某个分类下的所有产品
    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }

    //为多个分类填充推荐产品集合:把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8;
        for (Category category : categories) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductByRow(productsByRow);
        }
    }

    public void setSaleAndReviewNumber(Product product){  //添加销量和评价数量
        int saleCount = orderItemService.getSaleCount(product);//获得该商品总销量
        product.setSaleCount(saleCount);
        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);
    }

    public void setSaleAndReviewNumber(List<Product> products){
        for (Product product:products){
            setSaleAndReviewNumber(product);
        }
    }
    //初始化elasticsearch,如果没有数据则从数据库中保存
    private void initDatabase2ES(){
        Pageable pageable = new PageRequest(0, 5);
        Page<Product> page = productESDAO.findAll(pageable);
        if(page.getContent().isEmpty()){
            List<Product> products = productDAO.findAll();
            for(Product product:products){
                productESDAO.save(product);
            }
        }
    }
    //使用elasticsearch搜索
    public List<Product> search(String keyword, int start, int size) {
        initDatabase2ES();
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                .scoreMode("sum")
                .setMinScore(10);
        Sort sort  = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start, size,sort);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();

        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();
    }
}
