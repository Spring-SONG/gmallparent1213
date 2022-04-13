package com.atguigu.gmall1213.order.client.impl;

import com.atguigu.gmall1213.model.user.UserAddress;
import com.atguigu.gmall1213.order.client.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/13 9:58
 */
@Component
public class UserDegradeFeignClient implements UserFeignClient {


    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        return null;
    }
}
