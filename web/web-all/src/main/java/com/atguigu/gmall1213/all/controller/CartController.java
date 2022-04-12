package com.atguigu.gmall1213.all.controller;

import com.atguigu.gmall1213.cart.client.CartFeignClient;
import com.atguigu.gmall1213.model.product.SkuInfo;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/12 15:51
 */
@Controller
public class CartController {
    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    // http://cart.gmall.com/addCart.html?skuId=31&skuNum=1
    @RequestMapping("addCart.html")
    public String addCart(@RequestParam(name="skuId") Long skuId,
                          @RequestParam(name="skuNum") Integer skuNum,
                          HttpServletRequest request) {
        //添加购物车
        cartFeignClient.addToCart(skuId, skuNum);

        //通过skuId查询skuInfo,然后传给前台
        SkuInfo skuInfo=productFeignClient.getSkuInfoById(skuId);

        request.setAttribute("skuNum", skuNum);
        request.setAttribute("skuInfo", skuInfo);
        return "cart/addCart";
    }
    // 查询购物车列表控制器
    @RequestMapping("cart.html")
    public String cartList(){
        // 购物车列表页面
        return "cart/index";
    }
}
