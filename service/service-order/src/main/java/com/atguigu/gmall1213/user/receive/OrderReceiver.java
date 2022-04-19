package com.atguigu.gmall1213.user.receive;

import com.atguigu.gmall1213.model.enums.ProcessStatus;
import com.atguigu.gmall1213.model.order.OrderInfo;
import com.atguigu.gmall1213.user.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
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
}
