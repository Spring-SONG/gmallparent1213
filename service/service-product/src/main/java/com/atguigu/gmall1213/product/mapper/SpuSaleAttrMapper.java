package com.atguigu.gmall1213.product.mapper;

import com.atguigu.gmall1213.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author mqx
 * @date 2020/6/12 9:38
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    // 根据spuId查询销售属性列表
    List<SpuSaleAttr> selectSpuSaleAttrList(Long spuId);

    // 根据skuId spuId 查询销售属性集合数据
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("skuId") Long skuId, @Param("spuId") Long spuId);
}
