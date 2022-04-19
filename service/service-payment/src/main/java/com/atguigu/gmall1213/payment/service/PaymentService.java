package com.atguigu.gmall1213.payment.service;

import com.atguigu.gmall1213.model.order.OrderInfo;

public interface PaymentService {
    // 保存支付记录 数据来源应该是orderInfo
    void savePaymentInfo(String paymentType, OrderInfo orderInfo);
}
