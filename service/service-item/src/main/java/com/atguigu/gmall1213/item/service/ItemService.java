package com.atguigu.gmall1213.item.service;


import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * s商品详情模块，远程调用service-product-client中得数据
 */

public interface ItemService {
    /**
     * 通过skuId 获取数据， 如何确定返回值
     * @param skuId
     * @return 需要将不同部分数据分别放入map 集合中！
     */
    Map<String,Object> getBySkuId(Long skuId);
}
