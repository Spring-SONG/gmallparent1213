package com.atguigu.gmall1213.user.service;

import com.atguigu.gmall1213.model.enums.ProcessStatus;
import com.atguigu.gmall1213.model.order.OrderInfo;

import java.util.Map;

public interface OrderService {
    // 保存订单
    Long saveOrderInfo(OrderInfo orderInfo);

    /**
     * 获取流水号 ,将流水号放入缓存
     * @param userId 目的是用userId 在缓存中充当key保存流水号
     * @return
     */
    String getTradeNo(String userId);
    // 比较流水号
    boolean checkTradeNo(String tradeNo,String userId);
    // 删除流水号
    void deleteTradeNo(String userId);

    /**
     * 验证库存
     * @param skuId
     * @param skuNum
     * @return
     */
    boolean checkStock(Long skuId, Integer skuNum);

    OrderInfo getOrderInfo(Long orderId);

    void execExpiredOrder(Long orderId);

    void updateOrderStatus(Long orderId, ProcessStatus paid);

    void sendOrderStatus(Long orderId);

    /**
     * 将orderInfo 转化为map集合
     * @param orderInfo
     * @return
     */
    Map initWareOrder(OrderInfo orderInfo);
}
