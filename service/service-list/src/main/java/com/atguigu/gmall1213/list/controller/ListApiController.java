package com.atguigu.gmall1213.list.controller;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.list.service.SearchService;
import com.atguigu.gmall1213.model.list.Goods;
import feign.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/28 16:02
 */
@RestController
@RequestMapping("api/list")
public class ListApiController {
    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private SearchService searchService;

    public Result createIndex(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
        return Result.ok();
    }
    // 上架
    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable Long skuId){
        searchService.upperGoods(skuId);
        return Result.ok();
    }
    // 下架
    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable Long skuId){
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    //商品热度排名
    @GetMapping("inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable Long skuId){
        searchService.incrHotScore(skuId);
        return Result.ok();
    }
}
