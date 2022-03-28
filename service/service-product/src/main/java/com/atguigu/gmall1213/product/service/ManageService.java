package com.atguigu.gmall1213.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall1213.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    BaseAttrInfo getAttrInfo(Long attrId);

    /**
     * 分页查询 多个spuInfo 必须指定，查询第几页，每页显示的数据条数，是否有抽出条件 {category3Id=?}。
     * http://api.gmall.com/admin/product/{page}/{limit}?category3Id=61
     *
     * @param spuInfoPageParam
     * @param spuInfo          因为spuInfo 实体类的属性中有一个属性叫category3Id | spring mvc 封装对象传值
     * @return
     */
    IPage<SpuInfo> selectPage(Page<SpuInfo> spuInfoPageParam, SpuInfo spuInfo);

    /**
     * 获取所有的销售属性数据
     *
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuImage> getSpuImageList(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> selectPage(Page<SkuInfo> skuInfoPage);

    void onSale(Long skuId);

    SkuInfo getSkuInfo(Long skuId);

    BaseCategoryView getBaseCategoryViewBycategory3Id(Long category3Id);

    BigDecimal getSkuPriceBySkuId(Long sukId);

    /**
     * 根据skuId spuId 查询销售属性集合数据
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    /**
     * 根据spuId 查询数据
     * map.put("value_ids","skuId")
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(Long spuId);


    /**
     * 封装首页分类数据
     * @return
     */
    List<JSONObject> getBaseCategoryList();

    /**
     * 根据品牌Id 查询品牌数据
     * @param tmId
     * @return
     */
    BaseTrademark getBaseTrademarkByTmId(Long tmId);

    /**
     * 根据skuId 获取到平台属性，平台属性值。
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(Long skuId);
}
