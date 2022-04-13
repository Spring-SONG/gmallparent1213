package com.atguigu.gmall1213.order.client;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.order.client.impl.OrderDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(value = "service-order",fallback = OrderDegradeFeignClient.class)
public interface OrderFeignClient {

    // 获取数据接口
    @GetMapping("/api/order/auth/trade")
    Result<Map<String, Object>> trade();
}
