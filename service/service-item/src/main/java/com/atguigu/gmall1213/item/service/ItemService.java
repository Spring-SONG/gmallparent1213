package com.atguigu.gmall1213.item.service;


import org.springframework.stereotype.Service;

import java.util.Map;


public interface ItemService {
    Map<String,Object> getBySkuId(Long skuId);
}
