package com.atguigu.gmall1213.user.controller;

import com.atguigu.gmall1213.cart.client.CartFeignClient;
import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.common.util.AuthContextHolder;
import com.atguigu.gmall1213.model.cart.CartInfo;
import com.atguigu.gmall1213.model.order.OrderDetail;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.model.user.UserAddress;
import com.atguigu.gmall1213.order.client.UserFeignClient;
import com.atguigu.gmall1213.product.client.ProductFeignClient;
import com.atguigu.gmall1213.user.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Autowired
    private OrderService orderService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ProductFeignClient productFeignClient;


    @GetMapping("auth/trade")
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

    //提交订单
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo,
                              HttpServletRequest request) {
        // 用户Id
        String userId = AuthContextHolder.getUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));

        // 防止表单回退无刷新提交
        // 在后台能够获取页面提交过来的流水号
        // http://api.gmall.com/api/order/auth/submitOrder?tradeNo=null
        String tradeNo = request.getParameter("tradeNo");
        // 开始比较
        boolean flag = orderService.checkTradeNo(tradeNo, userId);
        // 如果返回的是true ，则说明第一次提交，如果是false 说明无刷新重复提交了！
//        if (flag){
//            // 正常
//        }else {
//            // 异常
//        }
        // 异常情况
        if (!flag){
            // 如果是false 说明无刷新重复提交了！
            return Result.fail().message("不能回退无刷新重复提交订单!");
        }
        // 创建一个集合对象，来存储异常信息
        List<String> errorList = new ArrayList<>();

        //创建一个异步编排的集合
        ArrayList<CompletableFuture> futureList=new ArrayList<>();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        if (!CollectionUtils.isEmpty(orderDetailList)) {
            for (OrderDetail orderDetail : orderDetailList) {
                //异步编排操作
                CompletableFuture<Void> checkStockCompletableFuture=CompletableFuture.runAsync(() -> {
                    // 调用查询库存方法
                    boolean result=orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
                    if (!result) {
                        // 提示信息某某商品库存不足
                        // return Result.fail().message(orderDetail.getSkuName()+"库存不足！");
                        errorList.add(orderDetail.getSkuName() + "库存不足！");
                    }
                }, threadPoolExecutor);
                futureList.add(checkStockCompletableFuture);

                // 利用另一个异步编排来验证价格
                CompletableFuture<Void> skuPriceCompletableFuture = CompletableFuture.runAsync(() -> {
                    // 获取到商品的实时价格
                    BigDecimal skuPrice = productFeignClient.getSkuPrice(orderDetail.getSkuId());
                    // 判断 价格有变化，要么大于 1 ，要么小于 -1。说白了 ,相等 0
                    if (orderDetail.getOrderPrice().compareTo(skuPrice) != 0) {
                        // 如果价格有变动，则重新查询。
                        // 订单的价格来自于购物车，只需要将购物车的价格更改了，重新下单就可以了。
                        cartFeignClient.loadCartCache(userId);
                        //return Result.fail().message(orderDetail.getSkuName()+"价格有变动,请重新下单！");
                        errorList.add(orderDetail.getSkuName()+"价格有变动,请重新下单！");
                    }
                }, threadPoolExecutor);
                // 将验证价格的异步编排添加到集合中
                futureList.add(skuPriceCompletableFuture);
            }
        }
        //合并线程所有的异步编排都在futureListh中
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
        if (errorList.size()>0){
            // 获取异常集合的数据
            return Result.fail().message(StringUtils.join(errorList,","));
        }
        // 删除流水号
        orderService.deleteTradeNo(userId);

        Long orderId = orderService.saveOrderInfo(orderInfo);
        // 返回用户Id
        return Result.ok(orderId);
    }

    @GetMapping("inner/getOrderInfo/{orderId}")
    public OrderInfo getOrderIfo(@PathVariable Long orderId) {

        return orderService.getOrderInfo(orderId);

    }
}
