package com.illegalaccess.delay.ui.admin.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@ComponentScan(basePackages = "com.illegalaccess.delay")
@SpringBootApplication
@EnableJpaAuditing // 使用jpa自动赋值
@EnableJpaRepositories(basePackages = "com.illegalaccess.delay.ui.modules")
@EntityScan(basePackages = "com.illegalaccess.delay.ui.modules")
@EnableCaching // 开启缓存
public class BootApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootApplication.class);
    }
}
