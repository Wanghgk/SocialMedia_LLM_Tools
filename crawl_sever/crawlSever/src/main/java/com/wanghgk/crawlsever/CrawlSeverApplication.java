package com.wanghgk.crawlsever;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

//启动类
@SpringBootApplication
public class CrawlSeverApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlSeverApplication.class, args);
    }

}
