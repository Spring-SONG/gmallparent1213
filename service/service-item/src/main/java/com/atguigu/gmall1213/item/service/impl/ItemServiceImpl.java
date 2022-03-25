package com.atguigu.gmall1213.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall1213.item.service.ItemService;
import com.atguigu.gmall1213.model.product.BaseCategoryView;
import com.atguigu.gmall1213.model.product.SkuInfo;
import com.atguigu.gmall1213.model.product.SpuSaleAttr;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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

    //自定义线程池
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String, Object> result=new HashMap<>();

        //通过异步编排封装商品详情数据,后边会用到返回值，使用supplyAsync返回
        CompletableFuture<SkuInfo> skuInfoCompletableFuture=CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo=productFeignClient.getSkuInfoById(skuId);
            result.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        //获取销售属性，销售属性值，需要skuInfo，并且没有返回值
        CompletableFuture<Void> spuSaleAttrCompletableFuture=skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            List<SpuSaleAttr> spuSaleAttrListCheckBySku=productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
        }), threadPoolExecutor);
        //查询分类数据，需要skuInfo的三级分类id
        skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            BaseCategoryView categoryView=productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
        }), threadPoolExecutor);
        //通过skuId获取价钱，runAsync 不需要返回值！
        CompletableFuture<Void> categoryViewCompletableFuture=CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice=productFeignClient.getSkuPrice(skuId);
            // 保存商品价格
            result.put("price", skuPrice);
        }, threadPoolExecutor);
        CompletableFuture<Void> priceCompletableFuture =categoryViewCompletableFuture;
        //获取价钱的方法二
//        skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
//            BigDecimal skuPrice=productFeignClient.getSkuPrice(skuInfo.getId());
//            result.put("price", skuPrice);
//        }), threadPoolExecutor);
        CompletableFuture<Void> valuesSkuJsonCompletableFuture=skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            Map skuValueIdsMap=productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            String skuValueJson=JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", skuValueJson);
        }), threadPoolExecutor);
        //热度排名计算，无返回值


        //将所有数据进行异步编排
        CompletableFuture.allOf(
                skuInfoCompletableFuture,
                spuSaleAttrCompletableFuture,
                categoryViewCompletableFuture,
                priceCompletableFuture,
                valuesSkuJsonCompletableFuture
        ).join();
        return result;
    }
}
