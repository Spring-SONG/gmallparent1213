package com.atguigu.gmall1213.user.service.impl;

import com.atguigu.gmall1213.model.user.UserAddress;
import com.atguigu.gmall1213.user.mapper.UserAddressMapper;
import com.atguigu.gmall1213.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/13 10:35
 */
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        List<UserAddress> userAddressList=userAddressMapper.selectList(new QueryWrapper<UserAddress>().eq("user_id", userId));
        return userAddressList;
    }
}
