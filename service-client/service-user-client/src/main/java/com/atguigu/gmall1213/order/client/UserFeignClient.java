package com.atguigu.gmall1213.order.client;

import com.atguigu.gmall1213.model.user.UserAddress;
import com.atguigu.gmall1213.order.client.impl.UserDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/13 9:50
 */
@FeignClient(value="service-user",fallback=UserDegradeFeignClient.class)
public interface UserFeignClient {

    // 获取数据接口
    @GetMapping("api/user/inner/findUserAddressListByUserId/{userId}")
    List<UserAddress> findUserAddressListByUserId(@PathVariable String userId);

}
