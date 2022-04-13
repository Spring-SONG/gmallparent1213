package com.atguigu.gmall1213.user.controller;

import com.atguigu.gmall1213.model.user.UserAddress;
import com.atguigu.gmall1213.user.service.UserAddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/7 15:19
 */
@RestController//当没有返回值的时候可以只用controller
@RequestMapping("api/user")
public class UserController {
    private UserAddressService userAddressService;
    @GetMapping("inner/findUserAddressListByUserId/{userId}")
    public List<UserAddress> findUserAddressListByUserId(@PathVariable String userId) {

        return userAddressService.findUserAddressListByUserId(userId);
    }
}
