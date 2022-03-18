package com.atguigu.gmall1213.item.client.impl;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.item.client.ItemFeignClient;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/18 14:53
 */
@Component
public class ItemDegradeFeignClient implements ItemFeignClient {

    @Override
    public Result getItem(Long skuId) {
        return null;
    }
}
