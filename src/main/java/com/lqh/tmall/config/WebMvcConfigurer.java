package com.lqh.tmall.config;

import com.lqh.tmall.interceptor.BackgroundInterceptor;
import com.lqh.tmall.interceptor.LoginInterceptor;
import com.lqh.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }

    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }

    @Bean
    public BackgroundInterceptor getBackgroundInterceptor(){
        return new BackgroundInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginIntercepter()).addPathPatterns("/**");
        registry.addInterceptor(getOtherIntercepter()).addPathPatterns("/**");
        registry.addInterceptor(getBackgroundInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin_login");
    }
}
