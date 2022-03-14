package com.atguigu.gmall1213.product.service;

import com.atguigu.gmall1213.model.product.BaseAttrInfo;
import com.atguigu.gmall1213.model.product.BaseCategory1;
import com.atguigu.gmall1213.model.product.BaseCategory2;
import com.atguigu.gmall1213.model.product.BaseCategory3;

import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2021/10/9 15:18
 */
public interface ManageService {

    //查询所有一级分类id
    List<BaseCategory1> getCategory1();

    //根据一级分类id查询二级分类数据
    List<BaseCategory2> getCategory2(Long category1Id);

    //根据二级分类id查询三级分类数据
    List<BaseCategory3> getCategory3(Long category2Id);

    //根据分类id查询平台属性值
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
