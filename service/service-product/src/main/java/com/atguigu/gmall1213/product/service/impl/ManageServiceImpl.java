package com.atguigu.gmall1213.product.service.impl;

import com.atguigu.gmall1213.model.product.*;
import com.atguigu.gmall1213.product.mapper.*;
import com.atguigu.gmall1213.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

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

    @Override
    public BaseAttrInfo getAttrInfo(Long attrId) {
        // attrId 平台属性Id  attrId=base_attr_info.id  此id 是base_attr_info 主键
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);

        // 判断如果当前baseAttrInfo 存在，则查询平台属性值集合。
        if (null!=baseAttrInfo){
            // 不能直接返回baseAttrInfo，因为控制器需要的是baseAttrInfo 下的平台属性值集合。
            // 需要给平台属性值属性赋值。
            // select * from base_attr_value where attr_id = attrId;
            QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper = new QueryWrapper<>();
            baseAttrValueQueryWrapper.eq("attr_id",attrId);
            List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(baseAttrValueQueryWrapper);
            // 将平台属性值结合放入baseAttrInfo 中，此时才能返回！
            baseAttrInfo.setAttrValueList(baseAttrValueList);
        }

        return baseAttrInfo;
    }

    @Override
    public IPage<SpuInfo> selectPage(Page<SpuInfo> spuInfoPageParam, SpuInfo spuInfo) {

        // 封装查询条件 where category3_id = ? order by id
        QueryWrapper<SpuInfo> spuInfoQueryWrapper = new QueryWrapper<>();
        spuInfoQueryWrapper.eq("category3_id",spuInfo.getCategory3Id());
        // 查询完成之后，可以按照某一种规则进行排序。
        spuInfoQueryWrapper.orderByDesc("id");
        return spuInfoMapper.selectPage(spuInfoPageParam,spuInfoQueryWrapper);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        // 调用mapper 层。
        return baseSaleAttrMapper.selectList(null);
    }
}
