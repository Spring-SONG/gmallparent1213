package com.atguigu.goodcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

//商品详情模块，用于数据汇总，数据要从service_product中远程调用，用户从web模块中查看
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置，有数据库jar包，配置文件不配置会报错
@ComponentScan({"com.atguigu.goodcode"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.atguigu.goodcode"})
public class ServiceGoodCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGoodCodeApplication.class, args);
    }

}
