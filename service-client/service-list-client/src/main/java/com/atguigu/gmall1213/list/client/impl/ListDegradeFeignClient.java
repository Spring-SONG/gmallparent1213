package com.atguigu.gmall1213.list.client.impl;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.list.client.ListFeignClient;
import com.atguigu.gmall1213.model.list.SearchParam;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/1 16:10
 */
@Component
public class ListDegradeFeignClient implements ListFeignClient {

    @Override
    public Result incrHotScore(Long skuId) {
        return null;
    }

    @Override
    public Result list(SearchParam listParam) {
        return null;
    }

    @Override
    public Result upperGoods(Long skuId) {
        return null;
    }

    @Override
    public Result lowerGoods(Long skuId) {
        return null;
    }
}
