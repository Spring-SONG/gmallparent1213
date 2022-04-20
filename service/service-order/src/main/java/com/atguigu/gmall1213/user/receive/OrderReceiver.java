package com.atguigu.gmall1213.user.receive;

import com.atguigu.gmall1213.model.enums.ProcessStatus;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.user.mapper.OrderInfoMapper;
import com.atguigu.gmall1213.user.service.OrderService;
import com.rabbitmq.client.Channel;
import common.constant.MqConst;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/19 14:32
 */
@Component
public class OrderReceiver {
    @Autowired
    private OrderService orderService;

    @SneakyThrows
    public void orderCancel(Long orderId, Message message, Channel channel) {
        if (null!=orderId) {
            OrderInfo orderInfo=orderService.getOrderInfo(orderId);
            if (null!=orderInfo && orderInfo.getOrderStatus().equals(ProcessStatus.UNPAID.name())) {
                orderService.execExpiredOrder(orderId);
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    //订单支付，更改订单状态并通知改库存
    @SneakyThrows
    @RabbitListener(bindings=@QueueBinding(
            value=@Queue(value=MqConst.QUEUE_PAYMENT_PAY, durable="true"),
            exchange=@Exchange(value=MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),
            key={MqConst.ROUTING_PAYMENT_PAY}
    ))
    public void updOrder(Long orderId, Message message, Channel channel) {

        if (null != orderId) {
            OrderInfo orderInfo=orderService.getOrderInfo(orderId);
            if (null != orderInfo && orderInfo.getOrderStatus().equals(ProcessStatus.UNPAID.getOrderStatus().name())) {
                //更新状态
                orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
                // 发送消息通知库存，准备减库存！
                orderService.sendOrderStatus(orderId);
            }
        }
        //手动确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
