package com.atguigu.gmall1213.cart.service.impl;

import com.atguigu.gmall1213.cart.mapper.CartInfoMapper;
import com.atguigu.gmall1213.cart.service.CartAsyncService;
import com.atguigu.gmall1213.model.cart.CartInfo;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/11 10:49
 */
@Service
public class CartAsyncServiceImpl implements CartAsyncService {

    @Autowired
    private CartInfoMapper cartInfoMapper;



    @Override
    @Async//异步方法
    public void updateCartInfo(CartInfo cartInfo) {
        System.out.println("更新的方法");
        cartInfoMapper.updateById(cartInfo);
    }

    @Override
    @Async
    public void saveCartInfo(CartInfo cartInfo) {
        System.out.println("新增的方法");
        cartInfoMapper.insert(cartInfo);
    }
}
