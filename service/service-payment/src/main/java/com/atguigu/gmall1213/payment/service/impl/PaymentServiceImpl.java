package com.atguigu.gmall1213.payment.service.impl;

import com.atguigu.gmall1213.model.enums.PaymentStatus;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.model.payment.PaymentInfo;
import com.atguigu.gmall1213.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall1213.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/19 15:37
 */
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

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
}
