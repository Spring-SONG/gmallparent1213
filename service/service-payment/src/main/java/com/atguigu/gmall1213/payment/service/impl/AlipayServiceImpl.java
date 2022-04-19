package com.atguigu.gmall1213.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall1213.model.enums.PaymentType;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.order.client.OrderFeignClient;
import com.atguigu.gmall1213.payment.service.AlipayService;
import com.atguigu.gmall1213.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/19 15:35
 */
@Service
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private AlipayClient alipayClient;
    @Override
    public String aliPay(Long orderId) throws AlipayApiException {
        // 获取订单对象
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        // 保存交易记录
        paymentService.savePaymentInfo(PaymentType.ALIPAY.name(),orderInfo);

        // 生产二维码
        AlipayTradePagePayRequest alipayRequest =  new  AlipayTradePagePayRequest(); //创建API对应的request
        // 同步回调
        alipayRequest.setReturnUrl( "http://domain.com/CallBack/return_url.jsp" );
        // 异步回调
        alipayRequest.setNotifyUrl( "http://domain.com/CallBack/notify_url.jsp" ); //在公共参数中设置回跳和通知地址
        // 声明一个集合
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",orderInfo.getTotalAmount());
        map.put("subject","买空调----");

        // 将map 转换为json字符串即可
        alipayRequest.setBizContent(JSON.toJSONString(map));
        // 直接将完整的表单html返回
        return alipayClient.pageExecute(alipayRequest).getBody();
    }
}
