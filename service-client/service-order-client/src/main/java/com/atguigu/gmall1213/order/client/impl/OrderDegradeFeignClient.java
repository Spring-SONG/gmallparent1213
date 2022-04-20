package com.atguigu.gmall1213.order.client.impl;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.order.client.OrderFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/13 11:19
 */
@Component
public class OrderDegradeFeignClient implements OrderFeignClient {
    @Override
    public Result<Map<String, Object>> trade() {
        return null;
    }
    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        return null;
    }
}

