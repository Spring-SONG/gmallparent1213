package com.atguigu.gmall1213.all.controller;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/25 14:29
 */
@Controller
public class IndexController {

    @Autowired
    private ProductFeignClient productFeignClient;


    @GetMapping({"/", "index.html"})
    public String index(HttpServletRequest request) {

        //調用遠程數據
        Result result=productFeignClient.getBaseCategoryList();

        //保存後臺獲取到的數據
        request.setAttribute("list", result.getData());
        return "index/index";

    }
}
