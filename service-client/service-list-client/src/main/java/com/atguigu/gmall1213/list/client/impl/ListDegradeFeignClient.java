package com.atguigu.gmall1213.list.client.impl;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.list.client.ListFeignClient;
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
}
