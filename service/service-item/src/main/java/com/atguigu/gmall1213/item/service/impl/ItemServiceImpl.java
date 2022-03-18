package com.atguigu.gmall1213.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall1213.item.service.ItemService;
import com.atguigu.gmall1213.model.product.BaseCategoryView;
import com.atguigu.gmall1213.model.product.SkuInfo;
import com.atguigu.gmall1213.model.product.SpuSaleAttr;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/17 14:18
 */
@Service
public class ItemServiceImpl implements ItemService {

    /*
    能获取到远程接口就能得到其中得数据
     */

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String, Object> result=new HashMap<>();

//获取skuinfo对象数据
        SkuInfo skuInfo=productFeignClient.getSkuInfoById(skuId);
        //通过skuId,spuId获取销售属性集合数据
        List<SpuSaleAttr> spuSaleAttrListCheckBySku=productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
        //通过category3Id获取分类数据
        BaseCategoryView categoryView=productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        //通过skuId获取价钱
        BigDecimal skuPrice=productFeignClient.getSkuPrice(skuId);
        //根据spuid获取由销售属性值id，和skuId组成的map集合数据
        Map skuValueIdsMap=productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
        //将结合转为json数据
        String skuValueJson=JSON.toJSONString(skuValueIdsMap);

        result.put("categoryView", categoryView);
        result.put("price", skuPrice);
        result.put("valuesSkuJson", spuSaleAttrListCheckBySku);
        result.put("skuInfo", skuInfo);


        return result;
    }
}
