package com.atguigu.gmall1213.product.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.model.product.*;
import com.atguigu.gmall1213.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: 整个类中的数据，都是为service-item 服务提供的
 * @Author: songshiqi
 * @Date: 2022/3/17 14:49
 */
@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    private ManageService manageService;

    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfoById(@PathVariable Long skuId){
        return manageService.getSkuInfo(skuId);

    }
    // 根据三级分类Id 查询分类名称
    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id){
        return manageService.getBaseCategoryViewBycategory3Id(category3Id);

    }

    //获取商品价格
    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId){
        return manageService.getSkuPriceBySkuId(skuId);
    }

    // 回显销售属性-销售属性值
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                          @PathVariable Long spuId){
        return manageService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }
    // 点击销售属性值进行切换
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId){
        return manageService.getSkuValueIdsMap(spuId);
    }

    @GetMapping("getBaseCategoryList")
    public Result getBaseCategoryList(){
        List<JSONObject> baseCategoryList = manageService.getBaseCategoryList();

        return Result.ok(baseCategoryList);
    }

    // 品牌信息
    @GetMapping("inner/getTrademark/{tmId}")
    public BaseTrademark getTrademarkByTmId(@PathVariable Long tmId){
        return manageService.getBaseTrademarkByTmId(tmId);
    }
    // 平台属性信息
    @GetMapping("inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId){
        List<BaseAttrInfo> attrInfoList = manageService.getAttrInfoList(skuId);
        return attrInfoList;
    }
}
