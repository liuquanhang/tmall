package com.lqh.tmall.web;

import com.lqh.tmall.pojo.Category;
import com.lqh.tmall.service.CategoryService;
import com.lqh.tmall.util.ImageUtil;
import com.lqh.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start",defaultValue = "0")int start,  //参数默认值
                                         @RequestParam(value = "size",defaultValue = "5")int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Category> page = categoryService.list(start,size,5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        return page;
    }

    //接受前台上传的图片,添加的分类信息，请求方式为post，调用saveOrUpdateImageFile保存图片
    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        categoryService.add(bean);
        saveOrUpdateImageFile(bean,image,request);
        return bean;
    }

    //上传并保存图片
    public void saveOrUpdateImageFile(Category bean,MultipartFile image,HttpServletRequest request) throws IOException {
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,bean.getId()+".jpg");  //以路径和分类名创建jpg文件
        if(!file.getParentFile().exists()) //检查文件目录是否存在
            file.getParentFile().mkdirs(); //不存在则创建目录
        image.transferTo(file); //复制文件到该路径
        BufferedImage img = ImageUtil.change2jpg(file); //使用ImageUtil转换图片格式为jpg
        ImageIO.write(img,"jpg",file);
    }

    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") int id,HttpServletRequest request){  //@PathVariable("id"):取出url中携带的id
        categoryService.delete(id);
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));   //获得目录的绝对路径
        File file = new File(imageFolder,id+".jpg");  //以路径和文件名创建file对象
        file.delete();  //删除图片
        return null;
    }

    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id) {
        Category bean=categoryService.get(id);
        return bean;
    }

    @PutMapping("/categories/{id}")  //后台以ajax函数put方式传回数据
    public Object update(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        String name = request.getParameter("name"); //获取请求中的name信息
        bean.setName(name);  //修改商品名称
        categoryService.update(bean);  //将商品信息更新至数据库
        if(image!=null){  //图片不为空则上传图片
            saveOrUpdateImageFile(bean,image,request);
        }
        return  bean;
    }
}


