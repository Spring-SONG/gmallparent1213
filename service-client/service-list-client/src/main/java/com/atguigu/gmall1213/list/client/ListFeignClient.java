package com.atguigu.gmall1213.list.client;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.list.client.impl.ListDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="service-list",fallback=ListDegradeFeignClient.class)
public interface ListFeignClient {
    // 发布接口 热度排名
    @GetMapping("api/list/inner/incrHotScore/{skuId}")
    Result incrHotScore(@PathVariable Long skuId);
}
