package com.atguigu.gmall1213.cart.controller;

import com.atguigu.gmall1213.cart.service.CartService;
import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.common.util.AuthContextHolder;
import com.atguigu.gmall1213.model.cart.CartInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/11 15:00
 */
@RestController
@RequestMapping("api/cart")
public class CartApiController {
    private CartService cartService;

    @PostMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId,
                            @PathVariable Integer skuNum,
                            HttpServletRequest request) {
        String userId=AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            userId=AuthContextHolder.getUserTempId(request);
        }
        cartService.addToCart(skuId,userId,skuNum);
        return Result.ok();
    }

    @GetMapping("cartList")
    public Result cartList(HttpServletRequest request) {
        //获取用户id
        String userId=AuthContextHolder.getUserId(request);
        //获取临时用户id
        String userTempId=AuthContextHolder.getUserTempId(request);

        List<CartInfo> cartList=cartService.getCartList(userId, userTempId);
        return Result.ok(cartList);
    }
}
