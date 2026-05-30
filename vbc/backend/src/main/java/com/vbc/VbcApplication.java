package com.vbc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.vbc.repository")
@EnableAsync
@EnableScheduling
public class VbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(VbcApplication.class, args);
    }
}
