package com.atguigu.goodcode.service.impl;

import com.atguigu.goodcode.mapper.GoodCodeMapper;
import com.atguigu.goodcode.service.GoodCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/29 10:35
 */
@Service
public class GoodCodeServiceImpl implements GoodCodeService {
    @Autowired
    private GoodCodeMapper goodCodeMapper;
    @Override
    public Map<String, Object> selectByCid() {
        return goodCodeMapper.selectByCid();
    }
}
