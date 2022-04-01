package com.atguigu.goodcode.controller;

import com.atguigu.goodcode.service.GoodCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/29 10:34
 */
@RestController
@RequestMapping("api/goodCode")
public class StringBuilderAppent {
    @Autowired
    private GoodCodeService goodCodeService;

    @GetMapping("/getMapKey")
    public void getMapKey(){
        StringBuilder stringBuilder=new StringBuilder("(");
        Map<String, Object> stringObjectMap=goodCodeService.selectByCid();
        for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
            String key=stringObjectEntry.getKey();
            stringBuilder.append("select * from " + key + " where cid = ");
        }
    }
}
