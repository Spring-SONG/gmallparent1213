package com.atguigu.gmall1213.mq.controller;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.mq.config.DeadLetterMqConfig;
import com.atguigu.gmall1213.mq.config.DelayedMqConfig;
import common.service.RabbitService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/15 16:52
 */
@RestController
@RequestMapping("/mq")
public class MqController {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("sendConfirm")
    public Result sendConfirm() {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        rabbitService.sendMessage("exchange.confirm", "routing.confirm",
                simpleDateFormat.format(new Date()));
        return Result.ok();
    }

    // 测试死信队列
    // 向队列一发送数据。
    @GetMapping("sendDeadLettle")
    public Result sendDeadLettle(){

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitTemplate.convertAndSend(DeadLetterMqConfig.exchange_dead, DeadLetterMqConfig.routing_dead_1, "ok");
        System.out.println(simpleDateFormat.format(new Date()+"  Delay sent....."));
        return Result.ok();
    }
    @GetMapping("sendDelay")
    public Result sendDelay(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitTemplate.convertAndSend(DelayedMqConfig.exchange_delay, DelayedMqConfig.routing_delay,
                simpleDateFormat.format(new Date()), new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {

                        message.getMessageProperties().setDelay(10*1000);
                        System.out.println(new Date()+" \t  Delay sent....");
                        return message;
                    }
                });

        return Result.ok();
    }
}
