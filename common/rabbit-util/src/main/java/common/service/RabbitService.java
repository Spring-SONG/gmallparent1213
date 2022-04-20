package common.service;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/15 16:07
 */
@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange,String routingKey,Object msg) {

        rabbitTemplate.convertAndSend(exchange,routingKey,msg);
        return true;

    }
    /**
     * 封装延续发送消息
     * @param exchange
     * @param routingKey
     * @param msg
     * @param delayTime
     * @return
     */
    public boolean sendDelayMessage(String exchange,String routingKey,Object msg,int delayTime){
        // 发送延迟消息
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置的延迟单位默认是毫秒，delayTime*1000  delayTime=2*60 两分钟
                message.getMessageProperties().setDelay(delayTime*1000);
                return message;
            }
        });
        return true;
    }
}
