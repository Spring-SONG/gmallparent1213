package com.atguigu.gmall1213.all.controller;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.item.client.ItemFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/18 14:49
 */
@Controller
public class ItemController {

    //注入远程调用接口
    private ItemFeignClient itemFeignClient;

    //页面渲染需要的数据接口url,model是作用域，用来存放页面渲染的数据
    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model) {
        Result<Map> item=itemFeignClient.getItem(skuId);
        model.addAllAttributes(item.getData());

        return "item/index";
    }
}
