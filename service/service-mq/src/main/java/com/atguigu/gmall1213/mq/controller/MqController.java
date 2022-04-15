package com.atguigu.gmall1213.mq.controller;

import com.atguigu.gmall1213.common.result.Result;
import common.service.RabbitService;
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

    @GetMapping("sendConfirm")
    public Result sendConfirm() {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        rabbitService.sendMessage("exchange.confirm", "routing.confirm",
                simpleDateFormat.format(new Date()));
        return Result.ok();
    }
}
