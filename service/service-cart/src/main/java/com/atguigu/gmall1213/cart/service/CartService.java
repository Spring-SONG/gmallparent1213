package com.atguigu.gmall1213.cart.service;

import com.atguigu.gmall1213.model.cart.CartInfo;

import java.util.List;

public interface CartService {
    // 添加购物车的接口
    void addToCart(Long skuId, String userId, Integer skuNum);

    // 查询购物车列表，查询登录，未登录
    List<CartInfo> getCartList(String userId, String userTempId);
}
