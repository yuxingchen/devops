package com.devops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.devops.mapper")
public class DeployPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeployPlatformApplication.class, args);
    }
} 