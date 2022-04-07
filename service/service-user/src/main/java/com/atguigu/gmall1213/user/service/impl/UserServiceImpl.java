package com.atguigu.gmall1213.user.service.impl;

import com.alibaba.nacos.common.util.Md5Utils;
import com.atguigu.gmall1213.model.user.UserInfo;
import com.atguigu.gmall1213.user.mapper.UserInfoMapper;
import com.atguigu.gmall1213.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/7 15:12
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;


    @Override
    public UserInfo login(UserInfo userInfo) {

        String newpwd=DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());

        //select * from user where username = ? and pwd = ?
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("login_name", userInfo.getLoginName()).eq("passwd", newpwd);
        UserInfo info=userInfoMapper.selectOne(queryWrapper);
        if (null!= info) {
            return info;
        }
        return null;
    }
}
