package com.atguigu.gmall1213.payment.service;

import com.alipay.api.AlipayApiException;

public interface AlipayService {
    // 如何定义这个接口？
    String aliPay(Long orderId) throws AlipayApiException;
}
