package com.atguigu.gmall1213.list.service.impl;

import com.atguigu.gmall1213.list.service.SearchService;
import com.atguigu.gmall1213.model.product.SkuInfo;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/28 16:55
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public void upperGoods(Long skuId) {

        SkuInfo skuInfo=productFeignClient.getSkuInfoById(skuId);
    }
}
