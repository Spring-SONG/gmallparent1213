package com.atguigu.gmall1213.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/13 10:08
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall1213"})
@EnableDiscoveryClient
public class ServiceUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }

}
