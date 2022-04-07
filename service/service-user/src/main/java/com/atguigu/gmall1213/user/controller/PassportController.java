package com.atguigu.gmall1213.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall1213.common.constant.RedisConst;
import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.common.util.IpUtil;
import com.atguigu.gmall1213.model.user.UserInfo;
import com.atguigu.gmall1213.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/7 15:21
 */
@RestController
@RequestMapping("/api/user/passport")
public class PassportController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request) {

        UserInfo info=userService.login(userInfo);

        if (null != info) {
            //登录成功后返回一个token，用来做登录状态
            String token=UUID.randomUUID().toString();
            //将token放到cookie中，根据前端数据是在map中存的token
            HashMap<Object, Object> map=new HashMap<>();
            map.put("token", token);
            //页面上的用户昵称
            map.put("nickName", info.getNickName());
            //将登录后的用户信息数据和本机的IP地址放入缓存，只需要一个用户id就可以
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("userId", info.getId().toString());
            jsonObject.put("ip", IpUtil.getIpAddress(request));
            //将数据放入缓存
            //定义key
            String key=RedisConst.USER_CART_KEY_SUFFIX + token;
            redisTemplate.opsForValue().set(key, jsonObject.toJSONString(), RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
            //将用户数据返回
            return Result.ok(map);
        } else {
            return Result.fail().message("用户名与密码不匹配");
        }
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request){

        String token=request.getHeader("token");
        String key=RedisConst.USER_CART_KEY_SUFFIX + token;
        redisTemplate.delete(key);
        return Result.ok();
    }
}
