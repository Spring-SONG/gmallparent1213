package com.atguigu.gmall1213.item.controller;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/17 14:20
 */
@RestController
@RequestMapping("api/item")
public class ItemApiController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}")
    public Result getItem(@PathVariable Long skuId){

        Map<String, Object> result=itemService.getBySkuId(skuId);

        return Result.ok(result);
    }
}
