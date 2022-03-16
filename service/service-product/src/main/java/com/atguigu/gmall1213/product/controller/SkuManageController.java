package com.atguigu.gmall1213.product.controller;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.model.product.SkuInfo;
import com.atguigu.gmall1213.model.product.SpuImage;
import com.atguigu.gmall1213.model.product.SpuSaleAttr;
import com.atguigu.gmall1213.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:http://api.gmall.com/admin/product/spuImageList/{spuId}
 * @Author: songshiqi
 * @Date: 2022/3/16 11:17
 */
@RestController
@RequestMapping("admin/product")
public class SkuManageController {

    @Autowired
    private ManageService manageService;

    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImageList=manageService.getSpuImageList(spuId);

        return Result.ok(spuImageList);
    }
    // 回显销售属性，属性值控制器
    // http://api.gmall.com/admin/product/spuSaleAttrList/{spuId}
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
        // 多个销售属性{ 销售属性中有消息属性值集合}
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);

        // 返回数据
        return Result.ok(spuSaleAttrList);

    }

    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);


        return Result.ok();
    }

    @GetMapping("list/{page}/{limit}")
    public Result getList(@PathVariable Long page,
                          @PathVariable Long limit){

        // 需要将page,limit 传给Page 对象。
        Page<SkuInfo> skuInfoPage = new Page<>(page,limit);
        IPage<SkuInfo> skuInfoIPage = manageService.selectPage(skuInfoPage);
        // 返回skuInfo 列表数据
        return Result.ok(skuInfoIPage);

    }

// 商品的上架
    // http://api.gmall.com/admin/product/onSale/34
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        manageService.onSale(skuId);
        return Result.ok();

    }

}
