package com.atguigu.gmall1213.list.service;

public interface SearchService {
    // 商品上架
    void upperGoods(Long skuId);
    // 商品下架
    void lowerGoods(Long skuId);

    void incrHotScore(Long skuId);
}
