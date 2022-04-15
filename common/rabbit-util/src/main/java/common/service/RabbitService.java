package common.service;

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
}
