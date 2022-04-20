package com.atguigu.gmall1213.payment.service;

import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.model.payment.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    // 保存支付记录 数据来源应该是orderInfo
    void savePaymentInfo(String paymentType, OrderInfo orderInfo);

    void paySuccess(String outTradeNo, String name, Map<String, String> paramMap);

    PaymentInfo getPaymentInfo(String out_trade_no, String name);

    void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfo);
}
