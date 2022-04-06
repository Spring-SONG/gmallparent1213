package com.atguigu.gmall1213.list.service;

import com.atguigu.gmall1213.model.list.SearchParam;
import com.atguigu.gmall1213.model.list.SearchResponseVo;

public interface SearchService {
    // 商品上架
    void upperGoods(Long skuId);
    // 商品下架
    void lowerGoods(Long skuId);

    void incrHotScore(Long skuId);
    /**
     * 检索数据
     * @param searchParam
     * @return
     * @throws Exception
     */
    SearchResponseVo search(SearchParam searchParam) throws Exception;
}
