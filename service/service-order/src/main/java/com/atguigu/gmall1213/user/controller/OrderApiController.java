package com.atguigu.gmall1213.user.controller;

import com.atguigu.gmall1213.cart.client.CartFeignClient;
import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.common.util.AuthContextHolder;
import com.atguigu.gmall1213.model.cart.CartInfo;
import com.atguigu.gmall1213.model.order.OrderDetail;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.model.user.UserAddress;
import com.atguigu.gmall1213.order.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/13 10:44
 */
@RestController
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    public Result<Map<String, Object>> trade(HttpServletRequest request) {
        String userId=AuthContextHolder.getUserId(request);

        //根据userId获取用户收获地址列表
        List<UserAddress> userAddressList=userFeignClient.findUserAddressListByUserId(userId);
        //获取送货清单
        List<CartInfo> cartCheckedList=cartFeignClient.getCartCheckedList(userId);
        List<OrderDetail> orderDetailList=new ArrayList<>();
        int totalNum=0;
        if (!CollectionUtils.isEmpty(cartCheckedList)) {
            //循环遍历赋值
            for (CartInfo cartInfo : cartCheckedList) {

                OrderDetail orderDetail=new OrderDetail();
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getSkuPrice());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setSkuId(cartInfo.getSkuId());

                totalNum+=cartInfo.getSkuNum();
                orderDetailList.add(orderDetail);
            }
        }
        // 声明一个map 集合来存储数据
        Map<String , Object> map = new HashMap<>();
        // 存储订单明细
        map.put("detailArrayList",orderDetailList);
        // 存储收货地址列表
        map.put("userAddressList",userAddressList);
        // 存储总金额
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        // 计算总金额
        orderInfo.sumTotalAmount();
        map.put("totalAmount",orderInfo.getTotalAmount());
        // 存储商品的件数 记录大的商品有多少个
        map.put("totalNum",orderDetailList.size());

        // 计算小件数：
        // map.put("totalNum",totalNum);

        return Result.ok(map);
    }
}
