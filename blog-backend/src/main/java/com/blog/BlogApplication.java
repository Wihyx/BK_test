package com.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 博客系统启动入口
 * - @SpringBootApplication 自动配置 Spring Boot + 内嵌 Tomcat
 * - @MapperScan 扫描 MyBatis-Plus Mapper 接口
 * - profiles: dev(H2) / prod(MySQL+Redis) 通过 application.yml 切换
 */
@SpringBootApplication
@MapperScan("com.blog.mapper")
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
