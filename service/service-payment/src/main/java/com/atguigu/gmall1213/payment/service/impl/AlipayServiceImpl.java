package com.atguigu.gmall1213.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.atguigu.gmall1213.model.enums.PaymentStatus;
import com.atguigu.gmall1213.model.enums.PaymentType;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.model.payment.PaymentInfo;
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

    @Override
    public boolean refund(Long orderId) {
        // out_trade_no 支付宝的交易编号，在orderInfo 中，还有paymentInfo 中都是同一个值！
        // AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","app_id","your private_key","json","GBK","alipay_public_key","RSA2");
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

        // 获取订单对象
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        // 声明一个map 集合来存储数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        map.put("refund_amount",orderInfo.getTotalAmount());
        map.put("refund_reason","空调不够凉！");
        // 支付的时候 支付的1块钱。
        // 退款能否退0.5 元？
        // map.put("out_request_no","HZ01RF001");
        // 传入json 字符串
        request.setBizContent(JSON.toJSONString(map));

        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            // 退款了。将交易
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setPaymentStatus(PaymentStatus.ClOSED.name());
            // 根据 out_trade_no 更新
            paymentService.updatePaymentInfo(orderInfo.getOutTradeNo(),paymentInfo);
            System.out.println("调用成功");
            return true;
        } else {
            System.out.println("调用失败");
            return false;
        }
    }
}
