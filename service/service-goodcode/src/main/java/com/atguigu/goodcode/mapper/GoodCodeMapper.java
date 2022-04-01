package com.atguigu.goodcode.mapper;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface GoodCodeMapper {
    Map<String, Object> selectByCid();
}
