package com.atguigu.gmall1213.cart.service;

import com.atguigu.gmall1213.model.cart.CartInfo;

import java.util.List;

public interface CartAsyncService {
    // 定义两个接口，要给是upd，一个insert
    /**
     * 修改购物车
     * @param cartInfo
     */
    void updateCartInfo(CartInfo cartInfo);

    /**
     * 保存购物车
     * @param cartInfo
     */
    void saveCartInfo(CartInfo cartInfo);
}
