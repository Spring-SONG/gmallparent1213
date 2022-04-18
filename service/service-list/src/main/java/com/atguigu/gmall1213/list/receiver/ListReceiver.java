package com.atguigu.gmall1213.list.receiver;

import com.atguigu.gmall1213.list.service.SearchService;
import com.rabbitmq.client.Channel;
import common.constant.MqConst;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/18 14:23
 */
@Component
public class ListReceiver {
    @Autowired
    private SearchService searchService;

    /**
     * 商品上架消息接收
     * @param skuId
     * @param message
     * @param channel
     */
    @RabbitListener(bindings=@QueueBinding(
            value=@Queue(value=MqConst.QUEUE_GOODS_UPPER, durable="true"),
            exchange=@Exchange(value=MqConst.EXCHANGE_DIRECT_GOODS),
            key=MqConst.QUEUE_GOODS_UPPER
    ))
    @SneakyThrows
    public void upperGoods(Long skuId, Message message, Channel channel) {

        if (null != skuId) {
            searchService.upperGoods(skuId);
        }
        //手动确认消息

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品下架消息接收者
     * @param skuId
     * @param message
     * @param channel
     */
    @RabbitListener(bindings=@QueueBinding(
            value=@Queue(value=MqConst.QUEUE_GOODS_LOWER, durable="true"),
            exchange=@Exchange(value=MqConst.EXCHANGE_DIRECT_GOODS),
            key=MqConst.QUEUE_GOODS_LOWER
    ))
    @SneakyThrows
    public void lowerGoods(Long skuId, Message message, Channel channel) {

        if (null != skuId) {
            searchService.lowerGoods(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
