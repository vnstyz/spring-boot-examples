package org.example.jdbcstudy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.jdbcstudy.mapper")
public class JdbcStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdbcStudyApplication.class, args);
    }

}
