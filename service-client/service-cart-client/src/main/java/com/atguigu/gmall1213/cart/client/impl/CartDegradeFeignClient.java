package com.atguigu.gmall1213.cart.client.impl;

import com.atguigu.gmall1213.cart.client.CartFeignClient;
import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.model.cart.CartInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/12 15:48
 */
@Component
public class CartDegradeFeignClient implements CartFeignClient {
    @Override
    public Result addToCart(Long skuId, Integer skuNum) {
        return null;
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        return null;
    }

    @Override
    public Result loadCartCache(String userId) {
        return null;
    }
}
