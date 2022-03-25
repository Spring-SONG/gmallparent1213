package com.atguigu.gmall1213.item.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/25 10:41
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor ThreadPoolExecutor(){
        return new ThreadPoolExecutor(
                50,//核心线程池数
                200,//最大线程池数
                30,//剩余空余线程的存活时间
                TimeUnit.SECONDS,//时间单位
                new ArrayBlockingQueue<>(50)//阻塞队列
        );
    }
}
