package com.atguigu.gmall1213.product.controller;

import com.atguigu.gmall1213.common.result.Result;
import com.atguigu.gmall1213.model.product.BaseTrademark;
import com.atguigu.gmall1213.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/3/15 14:15
 */
@RestController
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {


    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit){

        Page<BaseTrademark> baseTrademarkPage = new Page<>(page,limit);
        IPage<BaseTrademark> baseTrademarkIPage = baseTrademarkService.selectPage(baseTrademarkPage);
        // 返回Result 结果。
        return Result.ok(baseTrademarkIPage);
    }
    // 传递数据：id,imageUrl，tmName
    // vue 项目做保存的时候，传递的是Json 数据，后台需要使用java 对象接收。
    @PostMapping("save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        // 调用服务层
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }
    // http://api.gmall.com/admin/product/baseTrademark/update
    // 将前台的json 转化为java 对象
    @PutMapping("update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    // http://api.gmall.com/admin/product/baseTrademark/remove/{id}
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    // http://api.gmall.com/admin/product/baseTrademark/get/{id}
    @GetMapping("get/{id}")
    public Result getInfo(@PathVariable Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);

        return Result.ok(baseTrademark);
    }

}
