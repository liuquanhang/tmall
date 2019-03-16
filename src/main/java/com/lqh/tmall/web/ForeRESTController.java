package com.lqh.tmall.web;

import com.lqh.tmall.comparator.*;
import com.lqh.tmall.pojo.*;
import com.lqh.tmall.service.*;
import com.lqh.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.*;

//首页处理器
@RestController
public class ForeRESTController {
        @Autowired
        CategoryService categoryService;
        @Autowired
        ProductService productService;
        @Autowired
        UserService userService;
        @Autowired
        ProductImageService productImageService;
        @Autowired
        PropertyValueService propertyValueService;
        @Autowired
        ReviewService reviewService;
        @Autowired
        OrderItemService orderItemService;

    //向首页分类列表传递信息
    @GetMapping("/forehome")
    public Object home() {
        List<Category> cs= categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        System.out.println(cs);
        return cs;
    }

    //处理登陆操作
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){
            String name = user.getName();
            name = HtmlUtils.htmlEscape(name);
            user.setName(name);
            boolean exist = userService.isExist(name);
            if(exist){
                String message = "用户名已经被使用";
                return Result.fail(message);
            }
            userService.add(user);
            return Result.success();
        }

    //处理注册操作
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user =userService.get(name,userParam.getPassword());
        if(null==user){
            String message ="账号密码错误";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user);
            return Result.success();
        }
    }

    //商品页面
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid")int pid){
        Product product = productService.get(pid);
        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);  //在ProductImage表中查询该商品的singleImage
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);  //在ProductImage表中查询该商品的DetailImage
        product.setProductSingleImages(productSingleImages);  //将以上两个图片集合添加到Product中
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);  //查询该商品所有属性
        List<Review> reviews = reviewService.list(product);  //查询该商品所有评价
        productService.setSaleAndReviewNumber(product);  //设置销售和评论数量
        productImageService.setFirstProdutImage(product);  //设置first图片

        HashMap<String, Object> map = new HashMap<>();
        map.put("product",product);
        map.put("pvs",pvs);
        map.put("reviews",reviews);
        return Result.success(map);  //向前端返回结果
    }


    //检查是否登录
    @GetMapping("forecheckLogin")
    public Object checkLogin(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user!=null){
            return Result.success();
        }else{
            return Result.fail("未登录");
        }
    }

    //处理各种排序
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        if(sort!=null){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;
                case "saleCount":
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;
                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;
                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        return category;
    }

    //  处理搜索
    @PostMapping("foresearch")
    public Object search(String keyword){
        if(keyword==null) {
            keyword = "";
        }
            List<Product> products = productService.search(keyword, 0, 20);
            productImageService.setFirstProdutImages(products);
            productService.setSaleAndReviewNumber(products);
            return products;
    }

    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid,num,session);
    }

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int oiid = 0;
        User user =(User)  session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);  //查找该用户所有订单(orderItem)
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){ // 发现该商品订单已存在,增加数量，更新orderitem,返回id
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }
        //没有发现该商品的订单,初始化
        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }

    @GetMapping("forebuy")
    public Object buy(String[] oiid,HttpSession session){
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for(String oi:oiid){
            int id = Integer.parseInt(oi);
            OrderItem orderItem = orderItemService.get(id);
            total += orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
            orderItems.add(orderItem);
        }
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        session.setAttribute("ois",orderItems);
        Map<String,Object> map = new HashMap<>();
        map.put("orderItems",orderItems);
        map.put("total",total);
        return Result.success(map);
    }

    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }

    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }

    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(HttpSession session,int pid,int num){
        User user = (User)session.getAttribute("user");
        if(user==null) {
            return Result.fail("未登录");
        }else{
            List<OrderItem> orderItems = orderItemService.listByUser(user);
            for(OrderItem orderItem:orderItems){
                if(orderItem.getProduct().getId()==pid){
                    orderItem.setNumber(num);
                    orderItemService.update(orderItem);
                    break;
                }
            }
        }
        return Result.success();
    }

    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user = (User)session.getAttribute("user");
        if(user==null){
            return Result.fail("未登录");
        }else{
            orderItemService.delete(oiid);
            return Result.success();
        }
    }
}
