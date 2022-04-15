package com.atguigu.gmall1213.mq.receiver;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/15 16:57
 */
@Configuration
@Component
public class ConfirmReceiver {

    //消息接收者
    @RabbitListener(bindings=@QueueBinding(
            value=@Queue(value="queue.confirm",autoDelete="false"),
            exchange=@Exchange(value="exchange.confirm",autoDelete="true"),
            key={"routing.confirm"}
    ))
    public void process(Message message, Channel channel) {

        //获取消息
        System.out.println("msg: \t"+new String(message.getBody()));
        //确认消息
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            System.out.println("有异常了："+e.getMessage());
            //判断消息是否已经被处理过一次
            if (message.getMessageProperties().getRedelivered()) {
                System.out.println("消息已经被处理过了");
                //给一个拒绝消息
                try {
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("消息已经返回队列");
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
