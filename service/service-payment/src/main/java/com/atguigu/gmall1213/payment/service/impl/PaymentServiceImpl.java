package com.atguigu.gmall1213.payment.service.impl;

import com.atguigu.gmall1213.model.enums.PaymentStatus;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.model.payment.PaymentInfo;
import com.atguigu.gmall1213.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall1213.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.constant.MqConst;
import common.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/19 15:37
 */
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private RabbitService rabbitService;


    @Override
    public void savePaymentInfo(String paymentType, OrderInfo orderInfo) {

        // 交易记录中如果有当前对应的订单Id 时，那么还能否继续插入当前数据。
        QueryWrapper<PaymentInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("order_id",orderInfo.getId());
        orderInfoQueryWrapper.eq("payment_type",paymentType);

        Integer count = paymentInfoMapper.selectCount(orderInfoQueryWrapper);
        if (count>0){
            return;
        }
        // 创建一个对象
        PaymentInfo paymentInfo = new PaymentInfo();

        // 给对象赋值
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());

        paymentInfoMapper.insert(paymentInfo);
    }

    @Override
    public void paySuccess(String outTradeNo, String name, Map<String, String> paramMap) {
        // 需要获取到订单Id
        PaymentInfo paymentInfo = this.getPaymentInfo(outTradeNo, name);
        // 如果当前订单交易记录 已经是付款完成的，或者是交易关闭的。则后续业务不会执行！
        if (paymentInfo.getPaymentStatus().equals(PaymentStatus.PAID.name())
                || paymentInfo.getPaymentStatus().equals(PaymentStatus.ClOSED.name())){
            return;
        }

        // 第一个参数更新的内容，第二个参数更新的条件
        PaymentInfo paymentInfoUPD = new PaymentInfo();
        paymentInfoUPD.setPaymentStatus(PaymentStatus.PAID.name());
        paymentInfoUPD.setCallbackTime(new Date());
        // 更新支付宝的交易号，交易号在map 中
        paymentInfoUPD.setTradeNo(paramMap.get("trade_no"));
        paymentInfoUPD.setCallbackContent(paramMap.toString());

        // 构造更新条件
        // update payment_info set trade_no = ？，payment_status=？ ... where out_trade_no = outTradeNo and payment_type = name
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("out_trade_no",outTradeNo).eq("payment_type",name);
        paymentInfoMapper.update(paymentInfoUPD,paymentInfoQueryWrapper);

        // 发送消息通知订单
        // 更新订单状态 订单Id 或者 outTradeNo
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,MqConst.ROUTING_PAYMENT_PAY,paymentInfo.getOrderId());
    }

    @Override
    public PaymentInfo getPaymentInfo(String out_trade_no, String name) {
        QueryWrapper<PaymentInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", out_trade_no).eq("payment_type", name);
        return paymentInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfo) {
        // 根据第三方交易编号更新交易记录。
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("out_trade_no",outTradeNo);
        paymentInfoMapper.update(paymentInfo,paymentInfoQueryWrapper);
    }
}
