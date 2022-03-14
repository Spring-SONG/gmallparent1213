package com.atguigu.gmall1213.product.service.impl;

import com.atguigu.gmall1213.model.product.*;
import com.atguigu.gmall1213.product.mapper.*;
import com.atguigu.gmall1213.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2021/10/9 15:27
 */
@Service
public class ManageServiceImpl implements ManageService {

    // 通常会调用mapper 层。
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;


    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> baseCategory1s=baseCategory1Mapper.selectList(null);
        return baseCategory1s;
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper=new QueryWrapper<>();
        wrapper.eq("category1_id",category1Id);
        return baseCategory2Mapper.selectList(wrapper);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper=new QueryWrapper<>();
        wrapper.eq("category2_id", category2Id);
        return baseCategory3Mapper.selectList(wrapper);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id,category2Id,category3Id);
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        // 在这个方法中，一个是插入数据功能，一个是修改数据功能
        // 一个是平台属性表；baseAttrInfo
        if (baseAttrInfo.getId() != null) {
            // 修改功能
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            // 插入数据
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        // 一个是平台属性值表： baseAttrValue  插入的时候，直接insert ，
        // 修改{ 一：update，二：【先将数据删除，然后新增】}

        // 删除数据 有条件的删除
        // 根据传递过来的平台属性值Id 进行删除

        // 删除的是value所以跟infoid没关系是吧，删除平台属性下的平台属性值！
        QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper=new QueryWrapper<>();
        baseAttrValueQueryWrapper.eq("attr_id", baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueQueryWrapper);

        List<BaseAttrValue> attrValueList=baseAttrInfo.getAttrValueList();
        if (null != attrValueList && attrValueList.size() > 0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                // 页面在提交数据的时候，并没有给attrId 赋值，所以在此处需要手动赋值
                // attrId = baseAttrInfo.getId();
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                // 循环将数据添加到数据表中
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }
}
